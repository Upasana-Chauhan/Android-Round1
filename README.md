# Android-Round1
Target: Verify Android development skills

## Assignment
You have a [list of webviews](http://appcontent.hotelquickly.com/en/1/android/index.json) with defined URLs. Create an application with a list of all webviews. Click on a webview name should open new screen with webview content.  

Webviews with parameter ```cache == true ``` should be **preloaded** in background. When user opens a webview, content should be load from cache. If content is downloading from network(not from cache), loading indicator should be shown.

Please write clean & maintainable code to implement the relevant functionality. Simply writing correct code isn't enough.

### Specification:
* Preload cacheable webview (so it's not load when user request anymore)
* Usage of thrid party libraries is allowed
* Don't worry about design, just use standard controls
* Handle fundamental lifecycle flow
* Use key from json as a name of webview
* Use this values for replacements in URLs:
	* {userId} = 276
	* {appSecretKey} = gvx32RFZLNGhmzYrfDCkb9jypTPa8Q
	* {currencyCode} = USD
	* {offerId} = 10736598
	* {selectedVouchers} = []
* Support API 8 (optional)

### Bonus questions
* If URL of webview changes, will application load content from new URL?
* We would like to have a link in webview, that will open some native screen in application. Is it possible? How would you do it?

### Todo
* In GitHub, fork this repository to you own account
* The solution, after finished, should be send as pull request to original HQ repository.
* Log your manhour and provide that infomation back in the pull request detail.
* Attach final APK in this repository
