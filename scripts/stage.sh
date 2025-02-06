./gradlew jsBrowserDistribution && \
mv build/dist/js/productionExecutable/index.html build/dist/js/productionExecutable/viewer.html && \
rm build/dist/js/productionExecutable/modManagerViewer.js.LICENSE.txt && \
cp -r build/dist/js/productionExecutable/* ../mod-manager-site-deploy/
