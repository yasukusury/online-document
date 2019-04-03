var title = $('title');
title.text(title.text() + json.name);
$('.book-cover')[0].src = json.cover.substr(1);
$('#book-name').text(json.name);
var book = json;
