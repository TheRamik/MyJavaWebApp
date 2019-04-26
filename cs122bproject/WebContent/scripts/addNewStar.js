
function handleInfoResult(resultDataString) {
	console.log("in info result");
	//resultDataJson = JSON.parse(resultDataString);

	// if login success, redirect to index.html page
	if (resultDataString["status"] == "success") {
		console.log("succes");
		//window.location.replace("/cs122bproject/index.html");
		window.alert("Success: New Star Added!");
	} else {
		console.log("show error message");
		console.log(resultDataString["message"]);
		window.alert("ERROR: Star Not Added");
		jQuery("#add_error_message").text(resultDataString["message"]);
	}
}


function addNewStarForm(formSubmitEvent) {
	console.log("submit customer info form");
	
	// important: disable the default action of submitting the form
	//   which will cause the page to refresh
	//   see jQuery reference for details: https://api.jquery.com/submit/
	formSubmitEvent.preventDefault();
		
	jQuery.get(
		"/cs122bproject/AddNewStar", 
		// serialize the login form to the data sent by POST request
		jQuery("#add_star_info").serialize(),
		(resultDataString) => handleInfoResult(resultDataString));

}

// bind the submit action of the form to a handler function
jQuery("#add_star_info").submit((event) => addNewStarForm(event));

