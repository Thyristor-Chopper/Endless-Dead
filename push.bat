@echo off
git config credential.helper "!f() { echo username=$GIT_USERNAME; echo password=$GIT_PASSWORD; }; f"
git push origin master
