function handleMovieResult(resultData) {
	console.log("handleSearchResult: populating movie rating table from resultData");
	
	var searchResultsTableBodyElement = jQuery("#results_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href = StarPage.html?s_name=" + resultData[i]["star_name"]
					+ ">" + resultData[i]["star_name"] + "</a></td>";
		rowHTML += "<td>" + resultData[i]["dob"] + "</td>";
		var movieList = resultData[i]["movie_list"];
		var movies = movieList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < movies.length; j++) {
			var movie = movies[j].split(":");
			rowHTML += "<a href = \"MoviePage.html?m_id=" + movie[0] + "&title=\">" + movie[1] + "</a>";
		}
		rowHTML += "</td>";
		rowHTML += "</tr>"
		searchResultsTableBodyElement.append(rowHTML);
	}
}

function submitItemsForm(formSubmitEvent, qs) {
	formSubmitEvent.preventDefault();
	
	var params = qs.split('&');
	var x = false;
	var newQS = ""
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "items")){
			newQS += params[i] + "&";
		}
	}
	
	var itemNum = jQuery("#items_form").serialize();
	
	newQS += itemNum;
	window.location.search = newQS;
}

function handleStarResult(qs) {
	var params = qs.split('&');
	var x = false;
	var newQS = ""
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "items")){
			newQS += params[i] + "&";
		}
	}
	
	var itemNum = jQuery("#items_form").serialize();
	
	newQS += itemNum;
	window.location.search = newQS;
}

var queryString = window.location.search.substring(1);

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/project2/StarPage",
	  data: queryString,
	  success: (resultData) => handleMovieResult(resultData)
});

jQuery("#items_form").submit((event) => submitItemsForm(event, queryString));

jQuery("#cart_form").submit((event) => submitToCartForm(event, queryString));

