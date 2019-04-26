
function handleMovieResult(resultData) {
	console.log("handleMovieResult: populating movie rating table from resultData");

	// populate the star table
	var movieTableBodyElement = jQuery("#movie_rating_table_body");
	for (var i = 0; i < Math.min(10, resultData.length); i++) {
		var rowHTML = "";
		rowHTML += "<tr>";
		rowHTML += "<td>" + "<a href = MoviePage.html?m_id=" + resultData[i]["movie_id"]
				+ ">" + resultData[i]["movie_title"] + "</a></td>";
		rowHTML += "<td>" + resultData[i]["movie_year"] + "</td>";
		rowHTML += "<td>" + resultData[i]["movie_director"] + "</td>";
		var genreList = resultData[i]["genre_list"];
		var genres = genreList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < genres.length; j++) {
			rowHTML += "<a href = \"showMovies.html?genre=" + genres[j] + "&title=\">" + genres[j] + "</a>";
		}
		rowHTML += "</td>";
		var starList = resultData[i]["star_list"];
		var stars = starList.split(", ");
		rowHTML += "<td>";
		for (var j = 0; j < stars.length; j++) {
			rowHTML += "<a href = \"StarPage.html?s_name=" + stars[j] + "\">" + stars[j] + "</a>";
		}
		rowHTML += "</td>";
		rowHTML += "<td>" + resultData[i]["ratings_rating"] + "</td>";
		rowHTML += "<td><button type=\"submit\"><i class=\"fa fa-plus-square\">" 
				+ "</button></td>";
		rowHTML += "</tr>"
		movieTableBodyElement.append(rowHTML);
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

// makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/cs122bproject/MovieList",
	  success: (resultData) => handleMovieResult(resultData)
});

jQuery.ajax({
	  dataType: "json",
	  method: "GET",
	  url: "/cs122bproject/GenreList",
	  success: (genreResultData) => handleGenreResult(genreResultData)
});

createTitleResult();

