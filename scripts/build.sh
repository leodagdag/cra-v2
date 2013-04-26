#!/bin/bash
clear
echo "--> Build release"

if [ !-e"$HOME/build" ]; then
	mkdir $HOME/build
fi

cd $HOME/build

echo "--> Cleanning..."
rm * -Rf

echo "--> Clone git repo..."
git clone https://fredericpatin@github.com/genesis-groupe/cra-v2.git
cd cra-v2

echo "--> Configure git..."
git config --local user.email "f.patin@genesis-groupe.com"


current_version=`tail build.version`
read -p "--> new versions [${current_version}] " new_version next_version
next_snapshot="${next_version}-SNAPSHOT"

echo "Update files to version ${new_version}..."
sed -i "s/${current_version}/${new_version}/g" build.version
sed -i "s/version=\"${current_version}\"/version=\"${new_version}\"/g" conf/application.conf
sed -i "s/\"version\": \"${current_version}\"/\"version\": \"${new_version}\"/g" package.json
git commit -a -m"Release ${new_version}"
git tag -a "${new_version}" -m "Release ${new_version}"


echo "-->build version"
npm install

echo "--> Compile client"
grunt dist

echo "--> Compile server"
play clean compile dist


echo "-->update next version to ${next_version}"
sed -i "s/${new_version}/${next_snapshot}/g" build.version
sed -i "s/version=\"${new_version}\"/version=\"${next_snapshot}\"/g" conf/application.conf
sed -i "s/\"version\": \"${new_version}\"/\"version\": \"${next_snapshot}\"/g" package.json

git commit -a -m"Prepare for next release[${next_snapshot}]"
echo "--> Push to github"
#git push --mirror


send_to_server.sh
