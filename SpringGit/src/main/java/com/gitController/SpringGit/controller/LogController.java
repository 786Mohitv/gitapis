package com.gitController.SpringGit.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Source;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.style.ToStringCreator;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.gitController.SpringGit.helper.LogResponse;
import com.gitController.SpringGit.response.CommitResponse;
import com.gitController.SpringGit.service.UploadService;

@RestController
@RequestMapping("log")

public class LogController {

	@GetMapping("fileLog")
	public LogResponse getFileDetails(@RequestParam("file_name") String fileName,
			@RequestParam("base_repo_path") String baseRepo) {
		List<CommitResponse> commitArrayList = new ArrayList();

		File file = new File("E:/local-files" + fileName);
		Git git = null;
		try {

			git = Git.open(new File(baseRepo));
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		Iterable<RevCommit> logs = null;
		try {
			logs = git.log().addPath(fileName).call();

		} catch (NoHeadException e1) {

			e1.printStackTrace();
		} catch (GitAPIException e1) {

			e1.printStackTrace();
		}
		for (RevCommit revCommit : logs)
			commitArrayList.add(new CommitResponse(revCommit.getId(), revCommit.getName(), revCommit.getCommitTime(),
					revCommit.getFullMessage()));

		LogResponse response = new LogResponse(commitArrayList);

		return response;
	}
	@Autowired
	private UploadService uploadService;
	@RequestMapping(path = "/uploadFile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> commitFile(@RequestParam("sample_file") MultipartFile mFile,
			@RequestParam("file_path") String filePath, @RequestParam("base_repo_path") String baseRepo,
			@RequestParam("commit_message") String commitMessage) {

		
		if (uploadService.uFile(filePath, baseRepo, commitMessage, mFile))
			return new ResponseEntity<String>("File commited succesfully", HttpStatus.OK);
		else
			return new ResponseEntity<String>("Internal service error", HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@RequestMapping(path = "/file/{commitId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<byte[]> getFile(@PathVariable("commitId") String commitId,
			@RequestParam("base_repo") String baseRepo, @RequestParam("file_path") String path) {
		ObjectId id = ObjectId.fromString(commitId);

		InputStream in = null;
		File file = null;
		try {

			Git git = Git.open(new File(baseRepo));
			RevWalk revWalk = new RevWalk(git.getRepository());
			RevCommit commit = revWalk.parseCommit(id);
			// and using commit's tree find the path
			RevTree tree = commit.getTree();
			TreeWalk treeWalk = new TreeWalk(git.getRepository());
			treeWalk.addTree(tree);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(PathFilter.create(path));
			if (!treeWalk.next()) {
				return null;
			}
			ObjectId objectId = treeWalk.getObjectId(0);
			ObjectLoader loader = git.getRepository().open(objectId);

			in = loader.openStream();

			byte[] contents = in.readAllBytes();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDisposition(ContentDisposition.attachment().filename("yourfile.pdf").build());
			return new ResponseEntity<>(contents, headers, HttpStatus.OK);

		} catch (IOException e1) {

			System.out.println(e1);
		}

		return new ResponseEntity<byte[]>(new byte[1], HttpStatus.NOT_FOUND);

	}

}
