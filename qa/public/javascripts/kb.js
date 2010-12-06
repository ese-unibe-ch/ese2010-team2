$(document).ready(resize);
window.onresize = resize;

function resize() {
    $('#questions').height($(window).height()-140);
    $('#details').height($(window).height()-140);
    $('#content').width($(window).width()-40);
};

function showAll() {
    resetLayout();
    $.get("/questions/all", function(response, status, request) {
            $("#questions").html(response);
    });
}

function hideFlash() {
	$('#flash').hide();
}

function showRecent() {
    resetLayout();
    $.get("/questions/recent", function(response, status, request) {
        $('#questions').html(response);
    });
}

function showDetails(url, title) {
    resetLayout();
    $('#details_headline').html(title);

    $.get(url, function(response, status, request) {
        $('#details').html(response);
    });
}

function showProfile(name) {
    $('#questions_box').hide();
    $('#details_box').width(1000);
    $('#details_headline').text('Your profile');
    $.get("/showUser/" + name, function(response, status, request) {
        $('#details').html(response);
    });
}

function preview(a) {
	var m = '<div class="preview" id="preview" \
	style="position: absolute; top: ' + event.pageY + 'px; left: ' + event.pageX + 'px;"> \
	<img src="' + a.href + '" width="100" height="100" /> \
	</div>';

	$(a).parent().append(m);
}

function hide_preview() {
	$("#preview").detach();
}

function resetLayout() {
    $('#questions_box').show();
    resize();
}

function submit_search() {
    var text = $('#search_text')[0].value;
    var menu = $('#search_mode')[0].value;

    search(text, menu);
}

function resubmit_search() {
    var text = $('#search_text2')[0].value;
    var menu = $('#search_mode2')[0].value;

    search(text, menu);
}

function showSimilar(id) {
	$("#similar").html("<img src=\"/public/images/loading.gif\" alt=\"loading..\" width=\"16\" height=\"16\" />");
	$.get("/similar/" + id, function(response, status, request) {
            $("#similar").html(response);
    });
}

function search(text, menu) {
    resetLayout();
    $('#questions_headline').html('Search results for <em>' + text + '</em>');
    $.get("/search?text=" + text + "&menu=" + menu, function(response, status, request) {
            $("#questions").html(response);
    });
}

function setHeadline(id, text) {
    $(id).html(text);
}

function setOneColumnView() {
    $('#questions_box').hide();
    $('#details_box').width(1000);
}

function like() {
	if($(this).hasClass('like')) {
		var was = 'like';
		var now = 'unlike';
		var url = likeComment({id: this.hash.substr(1)});
	} else {
		var was = 'unlike';
		var now = 'like';
		var url = unlikeComment({id: this.hash.substr(1)});
	}

	var comment = $(this).parents('div.comment');
	$.post(url, {authenticityToken: token()}, function(data) {
		comment.find('a.'+was).removeClass(was).addClass(now).attr('title', now);
		comment.find('a.' + now + ' img').attr('src', function(index, attr) {
			return attr.replace(was, now);
		}).attr('alt', now);
		if(data.likes > 0)
			comment.find('span.likes').text(data.likes>1 ? data.likes+' people like this' : data.likes+' person likes this').removeClass('no');
		else
			comment.find('span.likes').addClass('no');
	}, "json");
	return false;
}

function token() {
	return $('#footer input').val();
}


$(function() {
	$(".like, .unlike").click(like);
});
