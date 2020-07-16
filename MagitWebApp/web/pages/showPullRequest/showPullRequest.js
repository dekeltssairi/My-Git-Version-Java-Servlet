var SHOW_PULL_REQUEST = buildUrlWithContextPath("pullrequestpage");
var ACCEPT_PULL_REQUEST = buildUrlWithContextPath("acceptpullrequest");
var REJECT_PULL_REQUEST = buildUrlWithContextPath("rejectpullrequest");

function createAcceptButton(pullRequestItem) {
    var acceptButton=$('<input/>').attr({type:'button', name: pullRequestItem.m_ID, value: 'Accept'});
    acceptButton.click(function () {

        $.ajax({
            url: ACCEPT_PULL_REQUEST,
            data: {pullrequestID: this.getAttribute("name")},
            success: function(repository) {
                alert("Pull request accecpted");
                updatePullRequestList(repository);
            },
            error: function(e){
                console.log("error haaaaaaaaaaaaaa")
            }
        })
    });
    return acceptButton;
}

function createRejectButton(pullRequestItem) {
    var rejectButton=$('<input/>').attr({type:'button', name:pullRequestItem.m_ID, value: 'Reject'});
    rejectButton.click(function () {
        localStorage.setItem("pullrequestid", pullRequestItem.m_ID);
        window.location.href = "../rejectPullRequest/rejectPullRequest.html";
    });


    return rejectButton;
}

function createShowCommitsDeltaButton(pullRequestItem) {
    var showCommits=$('<input/>').attr({type:'button', name:pullRequestItem.m_ID, value: 'Show Committs'});
    showCommits.click(function () {
        localStorage.setItem("pullrequestid",this.getAttribute("name"));
        window.location.href = "../showCommitsPullRequest/showCommitsPullRequest.html";
    });
    return showCommits;
}

function addPullRequestItem(pullRequestItem) {
    var accecptBtn= createAcceptButton(pullRequestItem);
    var rejectBtn= createRejectButton(pullRequestItem);
    var ShowDeltaBtn = createShowCommitsDeltaButton(pullRequestItem);
    debugger;
    if (pullRequestItem.m_Status == "Open"){
        $('<tr>' + '<td>' + pullRequestItem.m_Target + '</td>' + '<td>' + pullRequestItem.m_Base + '</td>' + '<td>' + pullRequestItem.m_Message + '</td>' + '<td>' + pullRequestItem.m_Requester.m_Name + '</td>' + '<td>' + pullRequestItem.m_Date+ '</td>' + '<td>' + pullRequestItem.m_Status + '</td>'+ '</tr>').append(ShowDeltaBtn).append(rejectBtn).append(accecptBtn).appendTo($("#pullRequestList"));
    }
    else{
        $('<tr>' + '<td>' + pullRequestItem.m_Target + '</td>' + '<td>' + pullRequestItem.m_Base + '</td>' + '<td>' + pullRequestItem.m_Message + '</td>' + '<td>' + pullRequestItem.m_Requester.m_Name + '</td>' + '<td>' + pullRequestItem.m_Date+ '</td>' + '<td>' + pullRequestItem.m_Status + '</td>'+ '</tr>').append(ShowDeltaBtn).appendTo($("#pullRequestList"));
    }

}


function updatePullRequestList(repository) {
    var pullRequestArray = repository.m_PullRequest;

    $("#pullRequestList").empty();
    if (pullRequestArray.length > 0){
        $('<tr>' + '<th>' + 'Target' + '</th>' + '<th>' + 'Bsae' + '</th>' + '<th>' + 'Message' + '</th>' + '<th>' + 'Requester' + '</th>'  + '<th>' + 'Date' + '</th>' + '<th>' + 'Status' + '</th>'+ '<th></th>'+ '</tr>').appendTo($("#pullRequestList"));
    }

    for (var i = 0; i < pullRequestArray.length; i++){
        addPullRequestItem(pullRequestArray[i]);
    }
}

$(function () {
    debugger;
    $.ajax({
        url: SHOW_PULL_REQUEST,
        //data: {nameOfRepository: repositoryName},
        success: function (repository) {
            updatePullRequestList(repository);
        }, error: function (e) {
            console.log("error");
        }
    });

    $("#back").click(function () {
        window.location.href = "../singleRepository/singleRepository.html";
    });
});