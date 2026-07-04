@echo off
cd gdxhelper
git add .
git commit -m %1
cd ..
git add .
git commit -m %1
