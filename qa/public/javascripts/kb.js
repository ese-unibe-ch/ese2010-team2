$(document).ready(resize);
window.onresize = resize;

function resize() {
    $('#questions').height($(window).height()-140);
    $('#details').height($(window).height()-140);
};

function showAll() {
    resetLayout();
    $.get("/questions/all", function(response, status, request) {
            $("#questions").html(response);
    });
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

function resetLayout() {
    $('#questions_box').show();
    $('#details_box').width(560);
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
