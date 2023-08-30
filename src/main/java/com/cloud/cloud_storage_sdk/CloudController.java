package com.cloud.cloud_storage_sdk;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import scala.Option;
import scala.Some;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class CloudController {

	private static String provider;
	private static String storageKey;
	private static String storageSecret;
	private static Option<String> storageEndpoint;
	private static Option<String> storageRegion;

	@Autowired
	public CloudController(@Value("${cloud.provider}") String provider, @Value("${cloud.storageKey}") String storageKey,
			@Value("${cloud.storageSecret}") String storageSecret,
			@Value("${cloud.storageEndpoint}") String storageEndpoint,
			@Value("${cloud.storageRegion}") String storageRegion) {
		CloudController.provider = provider;
		CloudController.storageKey = storageKey;
		CloudController.storageSecret = storageSecret;
		CloudController.storageEndpoint = scala.Option.apply(storageEndpoint);
		CloudController.storageRegion = scala.Option.apply(storageRegion);
	}

	private static final int STORAGE_SERVICE_API_RETRY_COUNT = 3;
	private static final Map<String, BaseStorageService> storageServiceMap = new HashMap<>();

	@GetMapping("/upload")
	public static String upload(@RequestParam(name = "container") String container,
			@RequestParam(name = "objectKey") String objectKey, @RequestParam(name = "filePath") String filePath) {
		String dcontainer = URLDecoder.decode(container, StandardCharsets.UTF_8);
		String dobjectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);
		String dfilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);
		BaseStorageService storageService = getStorageService(provider);
		return storageService.upload(dcontainer, dfilePath, dobjectKey, Option.apply(false), Option.apply(1),
				Option.apply(STORAGE_SERVICE_API_RETRY_COUNT), Option.empty());
	}

	@GetMapping("/getUrl")
	public static String getSignedUrl(@RequestParam(name = "container") String container,
			@RequestParam(name = "objectKey") String objectKey) {
		String dcontainer = URLDecoder.decode(container, StandardCharsets.UTF_8);
		String dobjectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);

		BaseStorageService storageService = getStorageService(provider);
		return getSignedUrl(storageService, dcontainer, dobjectKey, provider);
	}

	public static String getSignedUrl(BaseStorageService storageService, String container, String objectKey,
			String cloudType) {
		int timeoutInSeconds = getTimeoutInSeconds();
		return storageService.getSignedURLV2(container, objectKey, Some.apply(timeoutInSeconds), Some.apply("r"),
				Some.apply("application/pdf"));
	}

	@GetMapping("/delete")
	public static void deleteFile(@RequestParam(name = "container") String container,
			@RequestParam(name = "objectKey") String objectKey, HttpServletResponse response) throws IOException {
		String dcontainer = URLDecoder.decode(container, StandardCharsets.UTF_8);
		String dobjectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);
		BaseStorageService storageService = getStorageService(provider);
		try {
			storageService.deleteObject(dcontainer, dobjectKey, Option.apply(false));
		} catch (Exception e) {
			System.out.println("A exception occured:" + e);
		}
	}

	@GetMapping("/download")
	public static void dowloadFile(@RequestParam(name = "container") String container,
			@RequestParam(name = "objectKey") String objectKey, @RequestParam(name = "filePath") String filePath) {
		String dcontainer = URLDecoder.decode(container, StandardCharsets.UTF_8);
		String dobjectKey = URLDecoder.decode(objectKey, StandardCharsets.UTF_8);
		String dfilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

		BaseStorageService storageService = getStorageService(provider);
		try {
			storageService.download(dcontainer, dobjectKey, dfilePath, Option.apply(false));
		} catch (Exception e) {
			System.out.println("A exception occured:" + e);
		}
	}

	private static BaseStorageService getStorageService(String storageType) {
		Option<String> storageEndpoint = CloudController.storageEndpoint;
		Option<String> storageRegion = CloudController.storageRegion;

		return getStorageService(storageType, storageKey, storageSecret, storageEndpoint, storageRegion);
	}

	private static BaseStorageService getStorageService(String storageType, String storageKey, String storageSecret,
			scala.Option<String> storageEndpoint, scala.Option<String> storageRegion) {
		String compositeKey = storageType + "-" + storageKey;
		if (storageServiceMap.containsKey(compositeKey)) {
			return storageServiceMap.get(compositeKey);
		}
		synchronized (CloudController.class) {
			StorageConfig storageConfig = new StorageConfig(storageType, storageKey, storageSecret, storageEndpoint,
					storageRegion);

			System.out.println("Storageconfig:" + storageConfig);
			BaseStorageService storageService = StorageServiceFactory.getStorageService(storageConfig);
			storageServiceMap.put(compositeKey, storageService);
		}
		return storageServiceMap.get(compositeKey);
	}

	private static int getTimeoutInSeconds() {
		String timeoutInSecondsStr = "100000";
		return Integer.parseInt(timeoutInSecondsStr);
	}

}
