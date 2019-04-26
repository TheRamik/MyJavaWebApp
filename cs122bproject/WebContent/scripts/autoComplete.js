/*
 * CS 122B Project 4. Autocomplete.
 * 
 * This Javascript code uses this library: https://github.com/devbridge/jQuery-Autocomplete
 * 
 * This example implements the basic features of the autocomplete search, features that are 
 *   not implemented are mostly marked as "TODO" in the codebase as a suggestion of how to implement them.
 * 
 * To read this code, start from the line "$('#autocomplete').autocomplete" and follow the callback functions.
 * 
 */

var map = new Object();

/*
 * This function is called by the library when it needs to lookup a query.
 * 
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	console.log("sending AJAX request to backend Java Servlet")
	var newQuery = 1;
	// TODO: if you want to check past query results first, you can do it here
	for (var x in map) {
		if (query == x) {
			newQuery = 0;
		}
	}
	if (Boolean(newQuery)) {
		// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "/cs122bproject/AutoComplete?query=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
	else {
		console.log("Grabbing data from cache")
		doneCallback( { suggestions: map[query] } );
	}


}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	var jsonData = JSON.parse(data);
	console.log(jsonData)
	var newQuery = 1;
	// TODO: if you want to cache the result into a global variable you can do it here
	for (var x in map) {
		if (query == x) {
			newQuery = 0;
		}
	}
	if (Boolean(newQuery)) {
		map[query] = jsonData;
	}
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: jsonData } );
}


/*
 * This function is the select suggestion hanlder function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"])
	if (suggestion["data"]["category"] == "Movie")
	{
		var url = suggestion["data"]["category"] + "Page.html" + "?m_id=" + suggestion["data"]["m_id"]
	}
	else
	{
		var url = suggestion["data"]["category"] + "Page.html" + "?s_name=" + suggestion["value"]
	}
	
	window.location = window.location.origin + "/cs122bproject/" + url;
	console.log(url)
	console.log(window.location.origin)
}


/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    groupBy: "category",
    // set delay time
    deferRequestBy: 300,
    // set minimum number of characters required to trigger autosuggestion
    minChars: 3,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button

