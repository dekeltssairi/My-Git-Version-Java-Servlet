var COMMIT_ACTION = buildUrlWithContextPath("commitaction");
var repository ;

$(function() {
    repository = JSON.parse(localStorage.getItem("repository"));
    $("#commitButton").on('click', function () {
        debugger;
        $.ajax({
            url: COMMIT_ACTION,
            data: {commitMsg: document.getElementById("commitmsg").value, nameOfRepository: repository.m_Name},
            timeout: 2000,
            success: function (r) {
                window.location.href = "../singleRepository/singleRepository.html";
            }
        });
    });
});