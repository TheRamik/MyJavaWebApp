function handleShowMoviesResult(resultData) {
	console.log("handleSearchResult: populating movie rating table from resultData");
	
	var moviesResultsTableBodyElement = jQuery("#results_table_body");
	for (var i = 0; i < resultData.length; i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href = MoviePage.html?m_id=" + resultData[i]["movie_id"]
				+ ">" + resultData[i]["movie_title"] + "</a></td>";
		rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
		rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
		rowHTML += "<td>" + resultData[i]["genre_list"] + "</td>";
		var starList = resultData[i]["star_list"];
		var stars = starList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < stars.length; j++) {
			rowHTML += "<a href = \"StarPage.html?s_name=" + stars[j] + "\">" + stars[j] + "</a>";
		}
		rowHTML += "</td>";
		rowHTML += "<td><button type=\"submit\"><i class=\"fa fa-plus-square\">" 
				+ "</button></td>";
		rowHTML += "</tr>"
		moviesResultsTableBodyElement.append(rowHTML);
	}
}

function handleGenreResult(resultData) {
	console.log("handleGenreResult: grab genre from resultData");

	// grab all the genre in database 
	var genreBodyElement = jQuery("#genre_body");
	for (var i = 0; i < resultData.length; i++) {
		var genreName = resultData[i]["genre_name"];
		var genreHTML = "";
		var genreTxt = "<a href= \"showMovies.html?genre=" + genreName 
						+ "&title=\">" + genreName + "</a>";
		genreHTML += genreTxt;
		if ((i + 1) % 6 == 0) {
			genreHTML += "<br>";
		} else {
			genreHTML += " | "
		}

		genreBodyElement.append(genreHTML);
	}
}

function createTitleResult() {
	console.log("handleTitleResult: populating genre from resultData");

	// create alphabet list
	var alphanumBodyElement = jQuery("#alphanum_body");
	var numbers = "0123456789";
	var alphabet = "abcdefghijklmnopqrstuvwxyz";
	for (var i = 0; i < numbers.length; i++) {
		var numHTML = "";
		var numTxt = "<a href= \"showMovies.html?genre=&title=" + numbers[i] + "\">" + numbers[i] + "</a>";
		numHTML += numTxt;
		if (i != 9)
			numHTML += " | ";
		else
			numHTML += "<br>";
		alphanumBodyElement.append(numHTML);
	}
	for (var i = 0; i < alphabet.length; i++) {
		var alphabetHTML = "";
		var alphabetTxt = "<a href= \"showMovies.html?genre=&title=" + alphabet[i] + "\">" + alphabet[i] + "</a>";
		alphabetHTML += alphabetTxt;
		if ((i + 1) % 13 == 0) {
			alphabetHTML += "<br>";
		} else {
			alphabetHTML += " | "
		}

		alphanumBodyElement.append(alphabetHTML);
	}
}

function handleSplitQuery(qs) {
	var ShowMovies = jQuery("#show_movie_by");
	var ShowHTML = "";
	var params = qs.split('&');
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(attr[0] == "genre" && attr[1] != "") {
			ShowHTML += "<h1>Showing Genre: " + attr[1] + "</h1>";
		} 
		else if (attr[0] == "title" && attr[1] != "") {
			ShowHTML += "<h1>Showing Title: " + attr[1] + "</h1>";
		}
	}
	ShowMovies.append(ShowHTML);
}

function handleTitleSortQuery(qs) {
	var SortByTitle = jQuery("#sort_title_head");
	var TitleHTML = "";
	var params = qs.split('&');
	var newQS = "showMovies.html?";
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "sortTitle") && !(attr[0] == "sortYear")){
			newQS += params[i] + "&";
		} 
	}
	TitleHTML += "<a href=\"" + newQS + "sortTitle=true\">Title</a>"
	
	SortByTitle.append(TitleHTML);
}

function handleYearSortQuery(qs) {
	var SortByYear = jQuery("#sort_year_head");
	var YearHTML = "";
	var params = qs.split('&');
	var newQS = "showMovies.html?";
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "sortTitle") && !(attr[0] == "sortYear")){
			newQS += params[i] + "&";
		} 
	}
	YearHTML += "<a href=\"" + newQS + "sortYear=true\">Year</a>"
	
	SortByYear.append(YearHTML);
}

function handlePageButtons(qs) {
	var pageButton = jQuery("#page_form");
	var params = qs.split('&');
	var newQS = "showMovies.html?";
	var pageNum = 1;
	var pageHTML = ""
	for(var i = 0; i < params.length; i++){
		var attr = params[i].split("=");
		if(!(attr[0] == "page")){
			newQS += params[i] + "&";
		} else {
			pageNum = attr[1];
		}
	}
	
	var prevPage = (parseInt(pageNum) - 1);
	if (prevPage > 0) {
		pageHTML += "<a href=\"" + newQS + "page=" + prevPage + "\" class=\"previous\"> &#8249;</a>";
	}
	var nextPage = (parseInt(pageNum) + 1);
	pageHTML += "<a href=\"" + newQS + "page=" + nextPage + "\" class=\"next\"> &#8250;</a>";
	
	pageButton.append(pageHTML);
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


var queryString = window.location.search.substring(1);
handlePageButtons(queryString);
handleSplitQuery(queryString);
handleTitleSortQuery(queryString);
handleYearSortQuery(queryString);

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/cs122bproject/ShowMovies",
	  data: queryString,
	  success: (resultData) => handleShowMoviesResult(resultData)
});

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/cs122bproject/GenreList",
	  success: (genreResultData) => handleGenreResult(genreResultData)
});

createTitleResult();

jQuery("#items_form").submit((event) => submitItemsForm(event, queryString));