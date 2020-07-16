var REJECT_URL = buildUrlWithContextPath("rejectpullrequest");


$(function() {
    var pullRequestID = localStorage.getItem("pullrequestid")

    $("#reject").on('click', function () {
        $.ajax({
            url: REJECT_URL,

            data: {rejectMsg: document.getElementById("rejectMessage").value, pullrequestID: pullRequestID},
            success: function () {
                window.location.href = "../showPullRequest/showPullRequest.html";
            }
        });
    });
});