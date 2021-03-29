function trim {
  local var="$*"
  var=${var##+([[:space:]])}
  var=${var%%+([[:space:]])} 
  echo ${var}
}

function getTokenCmd {
  local tokenFiles="$*"
  local sedCmd="sed "
  for tokenFile in $tokenFiles
  do
    while IFS='#' read -r item rest
    do
      if [[ -z ${item} ]]; then continue; fi
      if [[ $item != *"="* ]]; then continue; fi

      key=$(trim "${item%%=*}")
      if [[ -z ${key} ]]; then continue; fi

      #value=${(trim "${item#*=}")}
      value=`eval echo '$'"$key"`
      #if [[ -z ${value} ]]; then continue; fi

      sedCmd+=" -e \"s/{{[[:space:]]*${key}[[:space:]]*}}/$(echo ${value} | sed -e 's/[\/&$]/\\&/g')/g\""
    done < $tokenFile
  done

  echo ${sedCmd}
}

function addTokenCmd {
  local token="$1"
  local key=$2
  local value=$3
  newToken=${token}" -e \"s/{{[[:space:]]*${key}[[:space:]]*}}/$(echo ${value} | sed -e 's/[\/&$]/\\&/g')/g\""
  echo ${newToken}
}

function getRandomAscii {
  local length=$1
  dd if=/dev/urandom count=1 2> /dev/null | uuencode -m - | sed -ne 2p | cut -c-$length
}

function getPackageManifestItem {
  local packagemanifest="$1"
  local itemName="$2"
  itemValue=$(oc -n openshift-marketplace get packagemanifest ${packagemanifest} -o yaml | grep ${itemName}:  | awk '{print $2}')
  echo ${itemValue}
}
