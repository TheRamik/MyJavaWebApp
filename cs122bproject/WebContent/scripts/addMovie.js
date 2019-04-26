function handleInfoResult(resultDataString) {
	console.log("in info result");

	if (resultDataString["status"] == "success") {
		console.log("succes");
		window.alert("Success: New Movie Added!");
	} else {
		console.log("show error message");
		console.log(resultDataString["message"]);
		window.alert("Error: Movie Not Added");
		jQuery("#add_error_message").text(resultDataString["message"]);
	}
}


function addMovieForm(formSubmitEvent) {
	console.log("submit customer info form");

	formSubmitEvent.preventDefault();
		
	jQuery.get(
		"/cs122bproject/AddMovie", 
		jQuery("#add_movie_info").serialize(),
		(resultDataString) => handleInfoResult(resultDataString));

}

// bind the submit action of the form to a handler function
jQuery("#add_movie_info").submit((event) => addMovieForm(event));

