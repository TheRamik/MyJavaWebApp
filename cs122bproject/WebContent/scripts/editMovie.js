function handleInfoResult(resultDataString) {
	console.log("in info result");

	if (resultDataString["status"] == "success") {
		console.log("succes");
		window.alert("Success: Movie Updated!");
	} else {
		console.log("show error message");
		console.log(resultDataString["message"]);
		window.alert("Error: Movie Not Updated");
		jQuery("#add_error_message").text(resultDataString["message"]);
	}
}


function editMovieForm(formSubmitEvent) {
	console.log("submit customer info form");

	formSubmitEvent.preventDefault();
		
	jQuery.get(
		"/cs122bproject/AddMovie", 
		jQuery("#edit_movie_info").serialize(),
		(resultDataString) => handleInfoResult(resultDataString));

}

// bind the submit action of the form to a handler function
jQuery("#edit_movie_info").submit((event) => editMovieForm(event));