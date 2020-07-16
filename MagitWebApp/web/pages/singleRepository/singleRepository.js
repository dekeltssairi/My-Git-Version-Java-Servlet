var Repository = undefined;
var timeout = 3000;
var firstupdate = true;
var SINGLE_REOISUTIRY_URL = buildUrlWithContextPath("singlerepository");
var COMMIT_DETAILS_URL = buildUrlWithContextPath("pages/showccmmit/showccmmit");
var SWITCH_HEAD_BRANCH = buildUrlWithContextPath("switchheadbranch");
var CreateRTB = buildUrlWithContextPath("creatertb");
var PULL = buildUrlWithContextPath("pull");
var PUSH = buildUrlWithContextPath("push");
var COMMIT_ACTION = buildUrlWithContextPath("commitaction");
var MESSAGES_URL = buildUrlWithContextPath("messages");


//var IS_RTB = buildUrlWithContextPath("isrtb");
var repositoryName = localStorage.getItem("repositoryName");

function GetRepositoryFromServerAndUpdatePage() {
     $.ajax({
        url: SINGLE_REOISUTIRY_URL,
        data: {nameOfRepository: repositoryName},
        success: function (repo) {
            Repository = repo;
            updateAllPage();
            UpdateMessages();
         //   updateBranchesList();
         //   updateCommittsList();
            console.log("inside get repo success");
        }, error: function (repo) {
            console.log("error");
        }
    });
}

function addBrachesPointToCurrentCommit(commit, commitList){
    for (var j = 0; j < Repository.m_Magit.m_Branches.length; j++){
        var branch = Repository.m_Magit.m_Branches[j];
        if (branch.m_Commit.m_Sha1 == commit.m_Sha1){
            var li = document.createElement("li");
            li.appendChild(document.createTextNode(branch.m_Name));
            commitList.appendChild(li);
        }
    }
}

function createShowCommitFilesButton(commit) {

    var $bt=$('<input/>').attr({type:'button', name: commit.m_Sha1, value: 'Show Files'});
    $bt.click(function () {

        $.ajax({
            url: COMMIT_DETAILS_URL,
            data: {repositoryname: Repository.m_Name, sha1: this.getAttribute("name")},
            success: function(repositoryFile) {
                console.log("in showCommit event");
                localStorage.setItem('repositoryFile', JSON.stringify(repositoryFile));
                window.location.href = "../commitDetails/commitDetails.html";
            },
            error: function(e){
                console.log("error haaaaaaaaaaaaaa")
            }
        })
    });
    return $bt;
}

function isRTB(branchName, remoteBranches) {
    var isRemote = false;
    for(var i = 0 ; i < remoteBranches.length; i++){
        var fullName = remoteBranches[i].m_Name.split("/");
        var RBOriginName = fullName[1];

        if(RBOriginName == branchName){
            isRemote = true;
        }
    }
    return isRemote;
}

function findRB(rtbBranch, remoteBranches){
    var rbBranch;
    for(var i = 0 ; i < remoteBranches.length; i++){
        var fullName = remoteBranches[i].m_Name.split("/");
        var RBOriginName = fullName[1];

        if(RBOriginName == rtbBranch.m_Name){
            rbBranch = remoteBranches[i];
        }
    }
    return rbBranch;
}

function findRemoteBranchInRemote(branchName) {
        for (var i = 0; i < Repository.m_Remote.m_RemoteRepositroy.m_Magit.m_Branches.length; i++){
            if (Repository.m_Remote.m_RemoteRepositroy.m_Magit.m_Branches[i].m_Name == branchName){
                return Repository.m_Remote.m_RemoteRepositroy.m_Magit.m_Branches[i];
            }
        }
}

function isBranchInRRSameAsRB(branch) {
    var remoteBranchInRemote = findRemoteBranchInRemote(branch.m_Name);
    var remoteBranchInLocal = findRB(branch, Repository.m_Remote.m_RemoteBranches);
    if (remoteBranchInLocal.m_Commit.m_MainFolderSha_1 == remoteBranchInRemote.m_Commit.m_MainFolderSha_1){
        return true;
    }
    else{
        return false;
    }
}

function createPullButton() {
    var pullBtn = document.createElement("button");
    var text = document.createTextNode("Pull");
    pullBtn.appendChild(text);
    pullBtn.onclick = function() {
        // updateAllPage();
        if (isRTB(Repository.m_Magit.m_Head.m_ActiveBranch.m_Name, Repository.m_Remote.m_RemoteBranches)) {  // is head rtb
            if (Repository.m_Magit.m_Head.m_ActiveBranch.m_Commit.m_MainFolderSha_1 == Repository.m_WC.m_MainFolder.m_Sha_1) { // is wc clear
                var remoteBranch = findRB(Repository.m_Magit.m_Head.m_ActiveBranch, Repository.m_Remote.m_RemoteBranches);
                if (remoteBranch.m_Commit.m_MainFolderSha_1 == Repository.m_Magit.m_Head.m_ActiveBranch.m_Commit.m_MainFolderSha_1) {
                    $.ajax({
                        url: PULL,
                        data: {nameOfRepository: repositoryName},
                        success: function (r) {
                            GetRepositoryFromServerAndUpdatePage();
                            alert("Seccessfully Pull");

                        }, error: function (e) {
                            console.log("error");
                        }
                    });
                } else {
                    alert("there are things to push");
                }
            } else {
                alert("wc is not clear");
            }
        }
        else{
            alert("no rtb");
        }

    };
    return pullBtn;
}

function isNewBranchToPush(localHeadBranchName, branchesInRemote) {
    var newBracnhToPush = true;
    for (var i = 0; i < branchesInRemote.length; i++){
        if (localHeadBranchName == branchesInRemote[i].m_Name){
            newBracnhToPush = false;
        }
    }
    return newBracnhToPush;
}

function createPushButton() {
    var pushBtn = document.createElement("button");
    var text = document.createTextNode("Push");
    pushBtn.appendChild(text);

    pushBtn.onclick = function(){
        GetRepositoryFromServerAndUpdatePage();
           if (isNewBranchToPush(Repository.m_Magit.m_Head.m_ActiveBranch.m_Name,Repository.m_Remote.m_RemoteRepositroy.m_Magit.m_Branches )){
               $.ajax({
                   url: PUSH,
                   data: {headName: Repository.m_Magit.m_Head.m_ActiveBranch.m_Name, nameOfRepository: repositoryName},
                   //timeout: 2000,
                   success: function () {
                       alert("Successfully push");
                       GetRepositoryFromServerAndUpdatePage();

                   }, error: function (e) {
                       console.log("error");
                   }
               });
           }
            else{
                alert("This branch isn't new. Conn't push an exist branch in remote");
            }
    };
    return pushBtn;
}

function createPullRequestButton() {
    var prBtn = document.createElement("button");
    var text = document.createTextNode("Pull Request");
    prBtn.appendChild(text);

    prBtn.onclick = function(){
        if(Repository.m_Remote.m_RemoteRepositroy.m_Magit.m_PushedBranches.length == 0){
            alert("You must to push new branch before pull request!");
        } else{
            localStorage.setItem('repository', JSON.stringify(Repository));
            window.location.href = "../pullRequest/pullRequest.html";
        }
    };
    return prBtn;
}

function hasRTB(rbName) {
    var array = rbName.split("/");
    var rtbName = array[1];
    for (var i = 0; i < Repository.m_Magit.m_Branches.length; i++){
        if (rtbName == Repository.m_Magit.m_Branches[i].m_Name){
            return true;
        }
    }
    return false;
}

function addRbBranches() {
    for (var i = 0; i < Repository.m_Remote.m_RemoteBranches.length; i++) {
        var $bt=$('<input/>').attr({type:'button', commit: Repository.m_Remote.m_RemoteBranches[i].m_Commit.m_Sha1, name: Repository.m_Remote.m_RemoteBranches[i].m_Name, value: 'create RTB'});
        $bt.click(function () {
            $.ajax({
                url: CreateRTB,
                data: {nameOfBranch: this.getAttribute("name"), sha1CommitOfBranch: this.getAttribute("commit")},
                //timeout: 2000,
                success: function (r) {
                    GetRepositoryFromServerAndUpdatePage();
                    alert(r);
                }, error: function (e) {
                    console.log("error");
                }
            });
        });
        if (hasRTB(Repository.m_Remote.m_RemoteBranches[i].m_Name)){
            $('<li id="remote">' + Repository.m_Remote.m_RemoteBranches[i].m_Name + '</li>').appendTo($("#brancheslist"));
        }
        else{
            $('<li id="remote">' + Repository.m_Remote.m_RemoteBranches[i].m_Name + '</li>').append($bt).appendTo($("#brancheslist"));
        }

    }
}

function addLocalBranches() {
    for (var i = 0; i < Repository.m_Magit.m_Branches.length; i++){
        var $bt=$('<input/>').attr({type:'button', name: Repository.m_Magit.m_Branches[i].m_Name, value: 'Set as head'});
        $bt.click(function () {
            if  ($("#openChanged").text() == "Open changes!!!") {
                var r = confirm("You Have Open Changes, if you confirm the action you might lost data!");
                if (r == true) {
                    $.ajax({
                        url: SWITCH_HEAD_BRANCH,
                        data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
                        //timeout: 2000,
                        success: function (r) {
                            Repository = r;
                            GetRepositoryFromServerAndUpdatePage();
                        }, error: function (e) {
                            console.log("error");
                        }
                    });
                }
            }
            else{
                    $.ajax({
                        url: SWITCH_HEAD_BRANCH,
                        data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
                        //timeout: 2000,
                        success: function (r) {
                            Repository = r;
                            GetRepositoryFromServerAndUpdatePage();
                        }, error: function (e) {
                            console.log("error");
                        }
                    });
            }
        });
        var headBranchName = Repository.m_Magit.m_Head.m_ActiveBranch.m_Name;
        if(headBranchName == Repository.m_Magit.m_Branches[i].m_Name) {
            $('<li id="head">' + Repository.m_Magit.m_Branches[i].m_Name + '</li>').append($bt).appendTo($("#brancheslist"));

        } else{
            var branchName = Repository.m_Magit.m_Branches[i].m_Name;
            var $setAsHeadbt = createSetAsHeadButton(branchName);
            if(isRTB(branchName, Repository.m_Remote.m_RemoteBranches)){
                $('<li id="rtb">' + branchName + '</li>').append($setAsHeadbt).appendTo($("#brancheslist"));

            } else{
                $('<li>' + branchName + '</li>').append($setAsHeadbt).appendTo($("#brancheslist"));

            }
            // isRTB(branchName,function (result) {
            //     var names = result.toString().split(" ");
            //
            //     var $setAsHeadbt=$('<input/>').attr({type:'button', name: names[1], value: 'Set as head'});
            //     $setAsHeadbt.click(function () {
            //         $.ajax({
            //             url: SWITCH_HEAD_BRANCH,
            //             data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
            //             timeout: 2000,
            //             success: function (r) {
            //                 updateAllPage(repositoryName);
            //                 alert(r);
            //
            //             }, error: function (e) {
            //                 console.log("error");
            //             }
            //         });
            //     });
            //
            //     if(names[0] == "true"){
            //         $('<li id="rtb">' + names[1] + '</li>').append($setAsHeadbt).appendTo($("#brancheslist"));
            //     } else{
            //         $('<li>' + names[1] + '</li>').append($setAsHeadbt).appendTo($("#brancheslist"));
            //     }
            // });
        }
    }
}

function createSetAsHeadButton(branchName) {
    var $bt=$('<input/>').attr({type:'button', name: branchName, value: 'Set as head'});
    $bt.click(function () {
        $.ajax({
            url: SWITCH_HEAD_BRANCH,
            data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
            //timeout: 2000,
            success: function (r) {
                Repository = r;
                GetRepositoryFromServerAndUpdatePage();
            }, error: function (e) {
                console.log("error");
            }
        });
    });
    return $bt;
}

function addBranches(){
    for (var i = 0; i < Repository.m_Magit.m_Branches.length; i++){
        var headBranchName = Repository.m_Magit.m_Head.m_ActiveBranch.m_Name;

        var $bt=$('<input/>').attr({type:'button', name: Repository.m_Magit.m_Branches[i].m_Name, value: 'Set as head'});
        $bt.click(function () {
            if ($("#openChanged").text() == "Open changes!!!") {
                var r = confirm("You Have Open Changes, if you confirm the action you might lost data!");
                if (r == true) {
                    $.ajax({
                        url: SWITCH_HEAD_BRANCH,
                        data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
                        //timeout: 2000,
                        success: function (r) {
                            Repository = r;
                            GetRepositoryFromServerAndUpdatePage();
                            //headBranchName = this.getAttribute("name");
                            //alert(r);

                        }, error: function (e) {
                            console.log("error");
                        }
                    });
                }
            } else {
                $.ajax({
                    url: SWITCH_HEAD_BRANCH,
                    data: {nameOfBranch: this.getAttribute("name"), nameOfRepository: repositoryName},
                    //timeout: 2000,
                    success: function (r) {
                        Repository = r;
                        GetRepositoryFromServerAndUpdatePage();
                        //headBranchName = this.getAttribute("name");
                        //alert(r);

                    }, error: function (e) {
                        console.log("error");
                    }


                });
            }
        });
        if(headBranchName == Repository.m_Magit.m_Branches[i].m_Name){
            $('<li id="head">' + Repository.m_Magit.m_Branches[i].m_Name + '</li>').append($bt).appendTo($("#brancheslist"));

        } else{
            $('<li>' + Repository.m_Magit.m_Branches[i].m_Name + '</li>').append($bt).appendTo($("#brancheslist"));
        }
    }
}

function updateBranchesList(isLocal) {
    $("#brancheslist").empty();

    if(isLocal){
        addRbBranches();
        addLocalBranches();
    }
    else{
        addBranches();
    }
}

function updateCommittsList() {
    $("#commitList").empty();

    if (Repository.m_AcceccibleCommitts.length > 0){
        $('<tr>' + '<th>' + 'SHA 1' + '</th>' + '<th>' + 'Message' + '</th>' + '<th>' + 'Date' + '</th>' + '<th>' + 'Committer' + '</th>'  + '<th>' + 'Pointed branches' + '</th>'+ '</tr>').appendTo($("#commitList"));
    }

    for (var i = 0; i < Repository.m_AcceccibleCommitts.length; i++)
    {
        var commit = Repository.m_AcceccibleCommitts[i];
        var ul = document.createElement("list");
        addBrachesPointToCurrentCommit(commit, ul);
        var $bt= createShowCommitFilesButton(commit);
        $('<tr>' + '<td>' + commit.m_Sha1 + '</td>' + '<td>' + commit.m_Message + '</td>' + '<td>' + commit.m_Date + '</td>' + '<td>' + commit.m_Committer.m_Name + '</td>' + '<td><div  id="pointedBranchesColum">' + ul.innerHTML + '</div></td>' + '</tr>').append($bt).appendTo($("#commitList"));
    }
}

function addCollaborationButton() {
    var pullBtn = createPullButton();
    var pushBtn = createPushButton();
    var prBtn = createPullRequestButton();

    $("#collabotation").empty();
    $("#collabotation").append(pullBtn);
    $("#collabotation").append(" ");
    $("#collabotation").append(pushBtn);
    $("#collabotation").append(" ");
    $("#collabotation").append(prBtn);
}

function createShowPullRequestButton() {
    var ShowPullRequestsBtn = document.createElement("button");
    var text = document.createTextNode("Show Pull Requests");
    ShowPullRequestsBtn .appendChild(text);
    ShowPullRequestsBtn .onclick = function() {
        if (Repository.m_PullRequest.length > 0) {
            window.location.href = "../showPullRequest/showPullRequest.html";
        }
        else{
            alert("No Pull Requests Yet!")
        }
    };
    return ShowPullRequestsBtn ;
}

function addShowPullRequestButton() {
    var showPullRequestBtn = createShowPullRequestButton();
    $("#collabotation").empty();
    $("#collabotation").append(showPullRequestBtn);
}

function updateTitle() {
    var repositoryName = localStorage.getItem("repositoryName");
    $("#title").text(repositoryName);
    if(Repository.m_Remote !== undefined) {
        var remoteRepositoryName = Repository.m_Remote.m_RemoteRepsoitoryName;
        var remotePath = Repository.m_Remote.m_PathToRemote;
        var fullpath = remotePath.split("\\");
        var remoteUserName = fullpath[2];
        $("#rrName").text("Remote username: " +remoteUserName);
        $("#rrUsername").text("Remote Repository name: "+ remoteRepositoryName);
    }
    if (Repository.m_WC.m_MainFolder.m_Sha_1 != Repository.m_Magit.m_Head.m_ActiveBranch.m_Commit.m_MainFolderSha_1){
        $("#openChanged").text("Open changes!!!");
    }
    else{
        $("#openChanged").empty();
    }

}

function updateAllPage() {
    var isRemote = Repository.m_PullRequest != undefined;
    var isLocal = Repository.m_Remote != undefined;

    updateTitle();
    updateCommittsList();
    updateBranchesList(isLocal);

    if (isLocal){
        addCollaborationButton();
    }
    if (isRemote){
        addShowPullRequestButton();
    }
}

function setMessages(messages) {
    $("#msgarea").empty();
    var msgTitle = document.createElement("h4");
    msgTitle.style = "font-weight: bold";
    msgTitle.innerText = "Messages: ";
    ($("#msgarea")).append(msgTitle);
    for(var i = 0 ; i <messages.length;i++){
        var label = document.createElement("label");
        //label.innerText = "•";
        label.innerText = "•\t";
        label.style = "font-weight: bold";
        //label.innerText = "kjsbdcj";

        $('<p>'+ label.innerText + messages[i] +'</p>').appendTo($("#msgarea"));
    }
}

function UpdateMessages() {
    $.ajax({
        url: MESSAGES_URL,
        success: function(messages) {
            setMessages(messages);
        }
    });
}

$(function() {
    GetRepositoryFromServerAndUpdatePage();
    setInterval(GetRepositoryFromServerAndUpdatePage, timeout);// automatically updae page when done;
});

function isExistBranch(newbranch) {
    var branches = Repository.m_Magit.m_Branches;
    for (var i = 0; i < branches.length; i++){
        if (branches[i].m_Name == newbranch){
            return true;
        }
    }
    return false;
}

$(function () {
    $("#addnewbranch").submit(function () {
        var newbranch = this[0].value;
        if (newbranch ==""){
            alert("cannt add empty branch");
        }
        else if (isExistBranch(newbranch)){
            alert ("this name is allready exist, please slect anoter name");
        }
        else {
            $.ajax({
                //data: $(this).serialize(),
                data: {branchname: newbranch, nameOfRepository: Repository.m_Name},
                url: this.action,
                //timeout: 2000,
                success: function (newrepository) {
                    if (typeof newrepository == "string") {
                        alert(newrepository);
                    } else {
                        alert("succesfully added branch name " + newbranch);
                        GetRepositoryFromServerAndUpdatePage();
                    }
                },
                error: function (e) {
                    console.log("error in add branch");
                }
            });
        }
        $("#branchname").val("");
        return false;
    });
});


$(function() {
    $("#wc").click(function () {
        localStorage.setItem("repository", JSON.stringify(Repository));
        window.location.href = "../wc/wc.html";
    });
    $("#commit").click(function () {
        if (Repository.m_WC.m_MainFolder.m_RepositoryFiles.length == 0){
            alert("wc empty, cant commint empty wc, its also very stupid thing to do")
        }
        else{
            if (Repository.m_WC.m_MainFolder.m_Sha_1 !== Repository.m_Magit.m_Head.m_ActiveBranch.m_Commit.m_MainFolderSha_1) {
                localStorage.setItem("repository", JSON.stringify(Repository));
                window.location.href = "../Commit/Commit.html";
            } else {
                alert("State is clear! Nothing to commit");
            }
        }

    });

    $("#back").click(function () {
        window.location.href = "../UserDetails/UserDetails.html";
    });
});





