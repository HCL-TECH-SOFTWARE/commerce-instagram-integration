To test integration in local for Commerce store APIs , make the following the temporary changes.

1. In tooling

	1. Add the below condition in the intercept method:
		commerce-tooling/src/app/rest/interceptor.ts 
		|| request.url.indexOf('https://localhost:5443') === 0

	2. Add the below code to override the base url:
		commerce-tooling/src/app/features/social/services/social-main.service.ts :
		this.rootUrl = "https://localhost:5443";

	3. In Instagram Asset, for images to work with Instagram API'service:
		Hardcode a domain that is publicaly available
		commerce-tooling/src/app/features/social/components/social-media/social-media.component.ts
		
		getProducts()-
		const thumbnail = "https://commerce-preview.sbx0091.play.hclsofy.com" + item.thumbnail;					

		showProductDetails(product: any)-
		const thumbnail = "https://commerce-preview.sbx0091.play.hclsofy.com";


2. In ts-app

	Add localhosst to fix CORS issue:
	WC/xml/config/com.ibm.commerce.foundation/wc-component.xml
	<_config:property name="CORSAccessControlAllowOrigin" value="https://commerceinsights.ibmcloud.com,https://localhost:7443"/>
