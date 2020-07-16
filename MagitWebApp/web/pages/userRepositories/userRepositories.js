var FORK_URL = buildUrlWithContextPath("fork");
var LASTCOMMIT_URL = buildUrlWithContextPath("lastcommit");

function allreadyExistRepository(repositoryName) {
    var existRepository = false;

    for (var i = 0; i < userRepositories.length; i++){
        if (userRepositories[i].m_Name == repositoryName){
            existRepository = true;
        }
    }
    return existRepository;

}

$(function() {
    $("#back").click(function () {
        window.location.href = "../UserDetails/UserDetails.html";
    });

    var selectedUserRepositories = JSON.parse(localStorage.getItem('selecteduserreposiroies'));
    var userName = (localStorage.getItem("user"));
    localStorage.clear();
    if(selectedUserRepositories.length == 0){
        $("#RepositoriesTitle").text("There are no repositories to " + userName + " yet!");
    }else{
        $("#RepositoriesTitle").text("Repositories of " + userName +": ");
        $('<tr>' + '<th>' + 'Repository Name' +'</th>' + '<th>' +'Active branch'+'</th>' + '<th>'  +'Number of branches'+'</th>'+'<th>'+'Last commit date'+'</th>'+'<th>' +'Last commit message'+'</th>'+'<th></th>'+'</tr>').appendTo($("#repositories"))
    }
    $.each(selectedUserRepositories || [], function(index, repository){
        console.log("Adding rep #" + index + ": " + repository);
        // var string = repository.m_Name + repository.m_ActiveBranchName +"," + repository.m_numOfBranches +"," + repository.m_LastCommitDate + "," + repository.m_LastMessage;
        //$('<li>' + repository.m_Name + '</li>').appendTo($("#repositories"));

        var lastCommit;
        $.ajax({
            url: LASTCOMMIT_URL,
            data: {repositoryname: repository.m_Name, userToFork: userName},
            success: function(commit) {
                lastCommit = commit;
                var branchNumber = repository.m_Magit.m_Branches.length;
                if(repository.m_Remote !== undefined){
                    branchNumber += repository.m_Remote.m_RemoteBranches.length;
                }
                $('<tr>' + '<td>' +repository.m_Name+'</td>' + '<td>' +repository.m_Magit.m_Head.m_ActiveBranch.m_Name +'</td>' + '<td>'  + branchNumber +'</td>'+'<td>'+lastCommit.m_Date+'</td>'+'<td>' +lastCommit.m_Message + '</td>'+'</tr>').append($bt).appendTo($("#repositories"));

                //$('<li>' + repository.m_Name + ", " + repository.m_Magit.m_Head.m_ActiveBranch.m_Name + ", " +branchNumber + ", "+ lastCommit.m_Date +", "+ lastCommit.m_Message + '</li>').append($bt).appendTo($("#repositories"));
            },
            error: function(e){
                lastCommit = null;
                console.log("error haaaaaaaaaaaaaa")
            }
        })

        var $bt=$('<input/>').attr({type:'button', name: repository.m_Name, value: 'Fork'})
        $bt.click(function () {
            if (allreadyExistRepository(repository.m_Name)){
                alert("repository named " + repository.m_Name + " already exist, can't fork");
            }
            else{
                $.ajax({
                    url: FORK_URL,
                    data:{nameOfUser: userName, nameOfRepository: repository.m_Name},
                    type:'get',
                    cache:false,
                    success: function(r) {
                        debugger;
                        alert(r);
                        console.log("succesfully ajax request from click event");
                        localStorage.setItem('selected', JSON.stringify(repositories));
                        window.location.href = "../UserDetails/UserDetails.html";
                    }
                });
            }

        });
        //$("#repositories").append($bt);
    });
});