#!/bin/bash

my_dir="$(cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P)"
resource="nexus"
source ${my_dir}/../utils.sh
source ${my_dir}/../env.conf
source ${my_dir}/${resource}/env.conf

token_files="${my_dir}/../*.conf ${my_dir}/${resource}/*.conf"
token_cmd=$(getTokenCmd ${token_files})

nexus_template=${my_dir}/${resource}/nexus3-persistent-template.yaml
oc process -f ${nexus_template} \
    -p NEXUS_VERSION=${nexus_version} \
    -p VOLUME_CAPACITY=${nexus_pv_capacity} \
    -p MAX_CPU=${nexus_max_cpu} \
    -p MAX_MEMORY=${nexus_max_memory} \
    -p REQUESTED_MEMORY=${nexus_requested_memory} \
    -p REQUESTED_CPU=${nexus_requested_cpu} \
    | oc -n ${namespace} apply -f - 

i=0
while true 
do
    result=$(oc -n ${namespace} get dc nexus -o template='{{.status.availableReplicas}}')
    if [ ${result} -eq 1 ]; then 
        break
    fi
    ((i++))
    echo 'Waiting for nexus is ready ...' ${i}
    sleep 1
done

result=$(oc -n ${namespace} exec $(oc -n ${namespace} get pods | grep nexus | grep -v deploy | awk '{print $1}') -- cat /nexus-data/etc/nexus.properties | grep  nexus.scripts.allowCreation=true | wc -l)
if [ ${result} -eq 0 ]; then 
    oc -n ${namespace} exec $(oc -n ${namespace} get pods | grep nexus | grep -v deploy | awk '{print $1}') -- /bin/sh  -c "echo nexus.scripts.allowCreation=true >> /nexus-data/etc/nexus.properties"
    oc -n ${namespace} delete po $(oc -n ${namespace} get pods | grep nexus | grep -v deploy | awk '{print $1}') 

    i=0
    while true 
    do
        result=$(oc -n ${namespace} get dc nexus -o template='{{.status.availableReplicas}}')
        if [ ${result} -eq 1 ]; then 
            break
        fi
        ((i++))
        echo 'Waiting for nexus is ready ...' ${i}
        sleep 1
    done

    export NEXUS_PASSWORD=$(oc -n ${namespace} exec $(oc -n ${namespace} get pods | grep nexus | grep -v deploy | awk '{print $1}') -- cat /nexus-data/admin.password)

    ocp_domain=$(oc -n openshift-console get route console -o jsonpath='{.spec.host}' | sed "s/console-openshift-console.//g")
    nexus_url=http://nexus-${namespace}.${ocp_domain}
    ${my_dir}/${resource}/nexus3-change-admin-password.sh ${nexus_url} ${NEXUS_PASSWORD} ${nexus_admin_password}
    ${my_dir}/${resource}/nexus3-allow-anonymous-access.sh ${nexus_url} ${nexus_admin_password}
    ${my_dir}/${resource}/nexus3-configure.sh ${nexus_admin_user} ${nexus_admin_password} ${nexus_url}
fi
