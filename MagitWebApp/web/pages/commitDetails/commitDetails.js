function showFile (repositoryFile){
    var subFiles = repositoryFile.m_RepositoryFiles;
    var blob = document.createElement("label");
    var folder = document.createElement("label");
    blob.innerText = "-Blob";
    folder.innerText = "-Folder";
    if(subFiles !== undefined){
        $('<p>' + folder.innerText + " " +repositoryFile.m_Name + '</p>').appendTo($("#files"));

        var numOfSubFiles = subFiles.length;

        for (var i = 0; i < numOfSubFiles; i++){
            showFile(subFiles[i]);
        }
    }
    else{
        $('<p>' + blob.innerText +" " +repositoryFile.m_Name + '</p>').appendTo($("#files"));
    }
}
$(function () {
    var commitmainfolder = JSON.parse(localStorage.getItem("repositoryFile"));
    //localStorage.clear();

    showFile(commitmainfolder)

    $("#back").click(function () {
        window.location.href = "../singleRepository/singleRepository.html";
    });
});