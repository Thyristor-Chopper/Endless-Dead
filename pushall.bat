@echo off
cd gdxhelper
git config credential.helper "!f() { echo username=$GIT_USERNAME; echo password=$GIT_PASSWORD; }; f"
git push origin master
cd ..
git config credential.helper "!f() { echo username=$GIT_USERNAME; echo password=$GIT_PASSWORD; }; f"
git push origin master
