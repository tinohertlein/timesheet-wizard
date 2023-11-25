build-ImportFunction:
	rm -rf ./build
	./gradlew buildNativeLambda -x test
	unzip ./build/libs/*.zip
	cp bootstrap func $(ARTIFACTS_DIR)/
