#!/bin/bashe
echo "Started execution of the qtest uploader script"
#Assign names to the command line arguments
{
  while getopts ":u:p:r:s:i:t:h:n:" opt; do
    case $opt in
    u)
      userName="$OPTARG"
      ;;
    p)
      basedir="$OPTARG"
      ;;
    r)
      resultsPath="$OPTARG"
      echo Setting - "$OPTARG"
      ;;
    s)
      dryRun="$OPTARG"
      ;;
    i)
      projectId="$OPTARG"
      ;;
    h)
      host="$OPTARG"
      ;;
    t)
      testCycleId="$OPTARG"
      ;;
     n)
      projectName="$OPTARG"
      ;;
    \?)
      echo "Invalid option -$OPTARG" >&2
      exit
      ;;
    esac
  done
} || exit

#Switch into the target folder and cleanup JSON files before merging usinq JQ
{
  #  Switch
  cd "$basedir""/target"

} || {
  echo "Error while switching into target folder" >basherror.text
  exit
}

#Encode the results file
{
  encodedOutput=$(<"encoded_cucumber_json.txt")
} || {
  echo "Error while reading the encoded_cucumber_json" >basherror.text
  exit
}
echo Path - "$resultsPath"

#Creating payload with appropriate values
{
  echo '{ "userName" : "'"$userName"'", "resultsPath" : "'"$resultsPath"'", "testcycle" : "'"$testCycleId"'", "dryRun" : "'"$dryRun"'", "result" : "'"$encodedOutput"'", "projectId" : "'"$projectId"'"}' >payload.json
} || {
  echo "Error while creating the payload with appropriate values" #>basherror.text
  exit
}
echo "Completed execution of the qtest uploader script"
#Invoking service
{
  if [[ "$projectName" == "native" ]]; then
    pulseUrl=https://pulse-7.qtestnet.com/webhook/7738c698-17a6-4887-bd38-0bec652c60ca
  else
    pulseUrl=https://pulse-7.qtestnet.com/webhook/306d04b0-28b4-46d5-a3ba-b3c688d7c0d4
  fi
} || {
  echo "Error while encoding the results file" >basherror.text
  exit
}
{
  curl --insecure -X POST \
    $pulseUrl \
    -x $host \
    -H 'cache-control: no-cache' \
    -H 'content-type: application/json' \
    -d @payload.json
} || {
  echo "Error while invoking the service with payload with appropriate values" >basherror.text
  exit
}

#sh QTestUploaderSerenity.sh -u vinothraj -p /Users/vinothraj/IdeaProjects/hiscox-usa-portal-testsuite -r N://IT Projects/US Projects/70081 - Mustang Portal/5 Team/Requirements/Actor Sheets/Archive/Images -e desktop -s true -i 90354 -t 1787430 -d 1781739 -m 1781740