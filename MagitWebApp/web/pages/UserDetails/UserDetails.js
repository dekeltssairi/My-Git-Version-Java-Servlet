var chatVersion = 0;
var refreshRate = 1000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var CHAT_LIST_URL = buildUrlWithContextPath("chat");
var USERNAME_URL = buildUrlWithContextPath("username");
var REPOSITORIES_LIST_URL = buildUrlWithContextPath("repositories");
var UPLOAD_URL = buildUrlWithContextPath("upload");
var LASTCOMMIT_URL = buildUrlWithContextPath("lastcommit");
var MESSAGES_URL = buildUrlWithContextPath("messages");
var SINGLE_REOISUTIRY_URL = buildUrlWithContextPath("singlerepository");
var repositoryName = localStorage.getItem("repositoryName");
var userRepositories;

function refreshUsersList(users) {
    $("#userslist").empty();

    $.each(users || [], function (index, username) {
        console.log("Adding user #" + index + ": " + username);
        $('<li><button>' + username + '</button></li>').appendTo($("#userslist"));
    });

    $("#userslist").on('click','li', function () {
        debugger;
        console.log("on clicked");
        var userName = this.innerText;
        $.ajax({
            url: REPOSITORIES_LIST_URL,
            data:{name: userName},
            type:'get',
            cache:false,
            success: function(repositories) {
                console.log("succesfully ajax request from click event");
                localStorage.setItem('selecteduserreposiroies', JSON.stringify(repositories));
                localStorage.setItem('user', userName);
                window.location.href = "../userRepositories/userRepositories.html";
            }
        });
    });
}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function setUsernameInPage(username) {
    console.log("in set username function");
    console.log(username);
    $("#name").text(username.toString());
}



function refreshRepositoriesList(repositories) {
    userRepositories = repositories;
    console.log("in set repositories function");
    $("#repositorieslist").empty();
    var first = true;
    var i =0;
    $.each(repositories || [], function (index, repository) {
        if(first){
            $('<tr>' + '<th>' + 'Repository Name' +'</th>' + '<th>' +'Active branch'+'</th>' + '<th>'  +'Number of branches'+'</th>'+'<th>'+'Last commit date'+'</th>'+'<th>' +'Last commit message'+'</th>'+'<th></th>'+'</tr>').appendTo($("#repositorieslist"))
            first = false;
        }
        console.log("Adding rep #" + index + ": " + repository);

        var $bt=$('<input/>').attr({type:'button', name: repository.m_Name, value: 'Show Details'});
        $bt.click(function () {
            localStorage.setItem('repositoryName', repository.m_Name);
            window.location.href = "../singleRepository/singleRepository.html";
        });

        var lastCommit;
        $.ajax({
            url: LASTCOMMIT_URL,
            data: {repositoryname: repository.m_Name},
            success: function(commit) {
                lastCommit = commit;
                var branchNumber = repository.m_Magit.m_Branches.length;
                if(repository.m_Remote !== undefined){
                    branchNumber += repository.m_Remote.m_RemoteBranches.length;
                }
                $('<tr>' + '<td>' +repository.m_Name+'</td>' + '<td>' +repository.m_Magit.m_Head.m_ActiveBranch.m_Name +'</td>' + '<td>'  + branchNumber +'</td>'+'<td>'+lastCommit.m_Date+'</td>'+'<td>' +lastCommit.m_Message + '</td>'+'</tr>').append($bt).appendTo($("#repositorieslist"));
            },
            error: function(e){
                lastCommit = null;
                console.log("error haaaaaaaaaaaaaa")
            }
        })
        //$('<tr>' + '<td>' +repository.m_Name+'</td>' + '<td>' +repository.m_Magit.m_Head.m_ActiveBranch.m_Name +'</td>' + '<td>'  +repository.m_Magit.m_Branches.length +'</td>'+'<td>'+lastCommit.m_Date+'</td>'+'<td>' +lastCommit.m_Message + '</td>'+'</tr>').append($bt).appendTo($("#repositorieslist"));
    });


}
function ajaxRepositoriesList() {
    $.ajax({
        url: REPOSITORIES_LIST_URL,
        success: function(repositories) {
            refreshRepositoriesList(repositories);
            //updateIsOpenChanges(repositories);
        },
        error: function(e, status, message){
            console.log(status);
            console.log(message);
            console.log("error haaaaaaaaaaaaaa")
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // OnLoad, Set the UserName
    $.ajax({
        url: USERNAME_URL,
        success: function(username) {
            setUsernameInPage(username);
        }
    });
});

function setMessages(messages) {
    $("#msgarea").empty();
    var msgTitle = document.createElement("h4");
    msgTitle.style = "font-weight: bold";
    msgTitle.innerText = "Messages: ";var label = document.createElement("label");
    label.innerText = "•\t";
    label.style = "font-weight: bold";
    ($("#msgarea")).append(msgTitle);
    for(var i = 0 ; i <messages.length;i++){
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

function HasOpenChanges(userRepository) {
    var hasOpenChanges = userRepository.m_WC.m_MainFolder.m_Sha_1 != userRepository.m_Magit.m_Head.m_ActiveBranch.m_Commit.m_MainFolderSha_1
    return hasOpenChanges;
}


$(function() { // Define Refreshing the userList every Second, and Load Repositories List
    setInterval(ajaxUsersList, refreshRate);
    setInterval(UpdateMessages, refreshRate);
    ajaxRepositoriesList();

    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
});

$(function() { // Define the upload File Form
    $("#uploadForm").submit(function () {
        var file1 = this[0].files[0];
        var formData = new FormData();
        formData.append(file1.name, file1);

        $.ajax({
            method: 'POST',
            data: formData,
            url: UPLOAD_URL,
            processData: false, // Don't process the files
            contentType: false, // Set content type to false as jQuery will tell the server its a query string request
            timeout: 4000,
            error: function (e) {
                console.log("error upload");
            },
            success: function (r) {
                console.log("success!!!!! upload");
                alert(r);
                ajaxRepositoriesList();
            }
        });
        return false;
    });
});