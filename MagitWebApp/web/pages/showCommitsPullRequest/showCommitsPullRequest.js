var commit;
var delta;
var COMMITTS_LIST_FROM_BASE = buildUrlWithContextPath("committsListFromBase");
var COMMIT_DELTA_URL = buildUrlWithContextPath("committsdelta");

function createShowContentButton(repositoryFile) {
    var showContentButton =$('<input/>').attr({type:'button', name: repositoryFile.m_Content, value: 'Show Content'});
    showContentButton.click(function () {
        document.getElementById('input_text_area').value = this.getAttribute("name");
    });
    return showContentButton;
}

function showDelta(delta) {
    $("#addedfiles").empty();
    $("#deletedfiles").empty();
    $("#changedfiles").empty();
    var added = delta.m_Added;
    var deleted = delta.m_Deleted;
    var changed = delta.m_Changed;

    var firstAdded = true;
    var firstDeleted = true;
    var firstChanged = true;

    for(var i = 0; i<added.length; i++){
        var repositoryFile = added[i];
        var isBlob = repositoryFile.m_Content != undefined;
        if(firstAdded){
            $("#addedTitle").text("Added files:");
            firstAdded = false;
        }
        if (isBlob){
            var showContentBtn = createShowContentButton(repositoryFile);
            $('<li>' + added[i].m_Path+ '</li>').append(showContentBtn).appendTo($("#addedfiles"));
        }
        else{
            $('<li>' + added[i].m_Path + '</li>').appendTo($("#addedfiles"));
        }
    }
    for(var i = 0; i<deleted.length; i++){
        var repositoryFile = deleted[i];
        var isBlob = repositoryFile.m_Content != undefined;
        if(firstDeleted){
            $("#deletedTitle").text("Deleted files:");
            firstDeleted = false;
        }
        // if (isBlob) {
        //     var showContentBtn = createShowContentButton(repositoryFile);
        //     $('<li>' + deleted[i].m_Path + '</li>').append(showContentBtn).appendTo($("#deletedfiles"));
        //
        // } else{
            $('<li>' + deleted[i].m_Path + '</li>').appendTo($("#deletedfiles"));
        //}
    }
    for(var i = 0; i<changed.length; i++){
        var repositoryFile = changed[i];
        var isBlob = repositoryFile.m_Content != undefined;
        if(firstChanged){
            $("#changedTitle").text("Changed files: ");
            firstChanged = false;
        }
        if (isBlob) {
            var showContentBtn = createShowContentButton(repositoryFile);
            $('<li>' + changed[i].m_Path + '</li>').append(showContentBtn).appendTo($("#changedfiles"));

        } else{
            $('<li>' + changed[i].m_Path + '</li>').appendTo($("#changedfiles"));
        }
    }
}

function createShowDeltaWithParentButton(commit) {
    var deltaWithParentBtn=$('<input/>').attr({type:'button', name: commit.m_Sha1, value: 'Show Delta with parent'});
    deltaWithParentBtn.click(function () {
        $.ajax({
            url: COMMIT_DELTA_URL,
            data: {sha1: this.getAttribute("name")},
            success: function(delta) {
                showDelta(delta);
            },
            error: function(e){
                console.log("error haaaaaaaaaaaaaa")
            }
        })
        document.getElementById('input_text_area').value = "";
    });
    return deltaWithParentBtn;
}

function showCommitts(committs) {
    $("#commitList").empty();
    if (committs.length > 0){
        $('<tr>' + '<th>' + 'SHA 1' + '</th>' + '<th>' + 'Message' + '</th>' + '<th>' + 'Date' + '</th>' + '<th>' + 'Committer' + '</th>' + '<th>' + 'Delta' + '</th>'  +  '</tr>').appendTo($("#commitList"));
    }
    for (var i = 0; i < committs.length; i++)
    {
        var commit = committs[i];
        var ul = document.createElement("list");
        var deltaWithParentBtn = createShowDeltaWithParentButton(commit);
        if(i < committs.length-1){
            $('<tr>' + '<td>' + commit.m_Sha1 + '</td>' + '<td>' + commit.m_Message + '</td>' + '<td>' + commit.m_Date + '</td>' + '<td>' + commit.m_Committer.m_Name + '</td>' + '</tr>').append(deltaWithParentBtn).appendTo($("#commitList"));
        }else{
            $('<tr>' + '<td>' + commit.m_Sha1 + '</td>' + '<td>' + commit.m_Message + '</td>' + '<td>' + commit.m_Date + '</td>' + '<td>' + commit.m_Committer.m_Name + '</td>' + '</tr>').appendTo($("#commitList"));
        }
    }
}

$(function () {
    $("#back").click(function () {
        window.location.href = "../showPullRequest/showPullRequest.html";
    });
    var pullRequestId = JSON.parse(localStorage.getItem("pullrequestid"));
    $.ajax({
        url: COMMITTS_LIST_FROM_BASE,
        data: {pullrequestID: pullRequestId},
        success: function(committs) {
           showCommitts(committs);
        },
        error: function(e){
            console.log("error haaaaaaaaaaaaaa")
        }
    })
});