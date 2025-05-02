clear
# TOKEN SETUP
jf c add --user=arana@mgmresorts.com --interactive=true --url=https://mgmresorts.jfrog.io --overwrite=true 

# clean
#rm -rf package-lock.json && rm -rf .jfrog && rm -rf temp

# Setting variables

export JF_RT_URL="https://mgmresorts.jfrog.io" JFROG_NAME="mgmresorts" RT_REPO_VIRTUAL="gaming-hospitality-iac-terraform-dev-virtual"  JFROG_CLI_LOG_LEVEL="DEBUG" 
export  BUILD_NAME="example-terraform-xray" BUILD_ID="cmd.$(date '+%Y-%m-%d-%H-%M')"



echo " JFROG_NAME: $JFROG_NAME \n JF_RT_URL: $JF_RT_URL \n BUILD_NAME: $BUILD_NAME \n BUILD_ID: $BUILD_ID \n JFROG_CLI_LOG_LEVEL: $JFROG_CLI_LOG_LEVEL  \n"


#Configure the project's deployment repository. You should set the local repository you created.
jf terraform-config --repo-deploy=${RT_REPO_VIRTUAL}


#CD to directory which contains the modules. for example "aws" directory.
cd aws

# Xray Audit
jf audit --format=table --extended-table=true --secrets=true --iac=true  --sca=true --sast=true  --fail=true

# Publish modules to Artifactory:
jf terraform publish --namespace=example --provider=aws --tag=v0.0.1 --build-name=${BUILD_NAME} --build-number=${BUILD_ID} --module="terra-aws"

#You can exclude files and directories from being scanned by the commands using the --exclusions option. In this example, files and directories which include test or ignore anywhere in their path, won't be scanned.

#jf terraform publish --namespace=example --provider=aws --tag=v0.0.2 --exclusions="*test*;*ignore*"

## [Xray] scan packages
echo "\n\n**** JF: scan ****"
jf scan . --extended-table=true --format=simple-json  --watches "room-booking-test-watch"


# echo "\n\n**** Build Info ****\n\n"
# # build: bce:build-collect-env 
# jf rt bce ${BUILD_NAME} ${BUILD_ID}
# ## build: bag:build-add-git
# jf rt bag ${BUILD_NAME} ${BUILD_ID}
# # Build:publish
# jf rt bp ${BUILD_NAME} ${BUILD_ID} --detailed-summary=true

# ## [Xray]  bs:build-scan
# echo "\n\n**** Xray: Build Scan ****\n\n"
# jf bs ${BUILD_NAME} ${BUILD_ID} --rescan=true --format=table --extended-table=true --vuln=true --fail=false 


# ## XRAY sbom enrich    ref# https://docs.jfrog-applications.jfrog.io/jfrog-applications/jfrog-cli/cli-for-jfrog-security/enrich-your-sbom    
# echo "\n\n**** [XRAY] sbom enrich ****"
# find . -iname "*.cdx.json"  
# # jf se "build/resources/main/META-INF/sbom/application.cdx.json"





# # # clean
# # rm -rf package-lock.json && rm -rf .jfrog && rm -rf temp


# echo "\n\n**** DONE ****\n\n"
