function findOptinalBasesRec(commits, m_Branches) {
    var branches = [];
    if (commits != undefined){


        for (var i = 0; i < m_Branches.length; i++){
            if (m_Branches[i].m_Commit.m_Sha1 == commits[0].m_Sha1){
                branches.push(m_Branches[i].m_Name);
            }
        }

        branches =branches.concat(findOptinalBasesRec(commits[0].m_ParentsCommit, m_Branches)) ;
    }
    return branches;
}

function getCommit(selectTarget, m_Branches) {
    for (var i = 0; i< m_Branches.length; i++){
        if (m_Branches[i].m_Name == selectTarget){
            return m_Branches[i].m_Commit;
        }
    }
}

function findOptionalBases(selectTarget, repository) {
    var commit = getCommit(selectTarget, repository.m_Magit.m_Branches);
    var parent = commit.m_ParentsCommit;
    return findOptinalBasesRec(parent, repository.m_Magit.m_Branches);
}

$(function () {
    var repository = JSON.parse(localStorage.getItem("repository"));
    var rbList = repository.m_Remote.m_RemoteBranches;
    var branches = repository.m_Magit.m_Branches;
    var isFirst = true;
    for(var i = 0; i < branches.length; i++){
        if(isRTB(branches[i].m_Name, rbList)){
            if(isFirst){
                $("#titleRTB").text("RTB:");
            }
            $('<li>' + branches[i].m_Name + '</li>').appendTo($("#RTBlist"));
            isFirst = false;
        }
    }
    var selectTarget = document.getElementById("targetbranches");
    for (var i = 0; i <  repository.m_Remote.m_RemoteRepositroy.m_Magit.m_PushedBranches.length; i++){
        var branchName =   repository.m_Remote.m_RemoteRepositroy.m_Magit.m_PushedBranches[i];
        var option = document.createElement("option");
        option.text = branchName;
        selectTarget.add(option);
    }
    $("#basebranches").empty();
    var selectedBranch = selectTarget.options[selectTarget.selectedIndex].text;
    var baseBracnhes = findOptionalBases(selectedBranch, repository.m_Remote.m_RemoteRepositroy);
    var selectedBaseList = document.getElementById("basebranches");
    for (var i = 0; i < baseBracnhes.length; i++){
        var option = document.createElement("option");
        option.text = baseBracnhes[i];
        selectedBaseList.add(option);
    }

    selectTarget.onchange = function () {
        $("#basebranches").empty();
        var selectedBranch = selectTarget.options[selectTarget.selectedIndex].text;
        var baseBracnhes = findOptionalBases(selectedBranch, repository.m_Remote.m_RemoteRepositroy);
        for (var i = 0; i < baseBracnhes.length; i++){
            var option = document.createElement("option");
            option.text = baseBracnhes[i];
            selectedBaseList.add(option);
        }
    };

    $("#backpr").click(function () {
        window.location.href = "../singleRepository/singleRepository.html";
    });
});