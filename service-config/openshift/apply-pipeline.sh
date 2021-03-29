#!/bin/bash

my_dir="$(cd "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P)"
source ${my_dir}/../..//utils.sh
source ${my_dir}/../../env.conf
source ${my_dir}/env.conf

token_files="${my_dir}/../../*.conf ${my_dir}/*.conf"
token_cmd=$(getTokenCmd ${token_files})

github_secret=$(getRandomAscii 32)
token_cmd=$(addTokenCmd "${token_cmd}" "github_secret" "${github_secret}")
generic_secret=$(getRandomAscii 32)
token_cmd=$(addTokenCmd "${token_cmd}" "generic_secret" "${generic_secret}")

eval "${token_cmd} ${my_dir}/pipeline.yaml" | oc -n ${namespace_tools} apply -f -
