
function handleInfoResult(resultDataString) {
	resultDataJson = JSON.parse(resultDataString);
	
	console.log("handle customer info response");
	console.log(resultDataJson);
	console.log(resultDataJson["status"]);

	// if login success, redirect to index.html page
	if (resultDataJson["status"] == "success") {
		window.location.replace("/project2/CheckoutSuccess.html");
	} else {
		console.log("show error message");
		console.log(resultDataJson["message"]);
		jQuery("#login_error_message").text(resultDataJson["message"]);
	}
}


function submitCreditInfoForm(formSubmitEvent) {
	console.log("submit customer info form");
	
	// important: disable the default action of submitting the form
	//   which will cause the page to refresh
	//   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
		
	jQuery.post(
		"/project2/CustomerInfo", 
		// serialize the login form to the data sent by POST request
		jQuery("#customer-info-form").serialize(),
		(resultDataString) => handleInfoResult(resultDataString));

}

// bind the submit action of the form to a handler function
jQuery("#customer-info-form").submit((event) => submitCreditInfoForm(event));

