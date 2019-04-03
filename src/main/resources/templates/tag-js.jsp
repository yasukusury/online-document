<script>
    function tagButton(text, href){
        return '<div class="btn btn-default tag-btn"><a href="' + href + '">' + text + '</a></div>';
    }

    $(document).ready(function () {
        var hotTag = ${json(hotTag)};
        var maxTag = ${json(maxTag)};
            if (hotTag){
                var html = '';
                for (var i in hotTag) {
                    var datum = hotTag[i];
                    html += tagButton(datum, '/search/?keyword=' + datum);
                }
                $('#hotTagPanel').html(html);
            }
            if (maxTag){
                var html = '';
                for (var i in maxTag) {
                    var datum = maxTag[i];
                    html += tagButton(datum, '/search/?keyword=' + datum);
                }
                $('#maxTagPanel').html(html);
            }
    });
</script>