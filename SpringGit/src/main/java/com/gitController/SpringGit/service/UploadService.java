package com.gitController.SpringGit.service;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.util.FS;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

	public boolean uFile(String filePath, String baseRepo, String commitMessage, MultipartFile mFile) {

		int flag = 0;
		try {
			File file = new File(baseRepo+filePath + mFile.getOriginalFilename());
			file.getParentFile().mkdirs();
			mFile.transferTo(file);

			if (RepositoryCache.FileKey.isGitRepository(new File("e:/local-files" + baseRepo + ".git"), FS.DETECTED)) {
//				System.out.println("found");
				flag = 1;
				Git git = Git.open(new File("e:/local-files" + baseRepo));
				git.add().addFilepattern(filePath + mFile.getOriginalFilename()).call();
				git.commit().setMessage(commitMessage).call();
			} else {
//				System.out.println("not found");
				Git git = Git.init().setDirectory(new File("e:/local-files"+baseRepo)).call();
				git.add().addFilepattern(filePath + mFile.getOriginalFilename()).call();
				git.commit().setMessage(commitMessage).call();
			}

		} catch (Exception e) {
			return false;

		}
		return true;
	}

}
