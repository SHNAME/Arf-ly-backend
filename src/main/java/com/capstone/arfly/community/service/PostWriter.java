package com.capstone.arfly.community.service;

import com.capstone.arfly.common.domain.File;
import com.capstone.arfly.common.dto.FileDetailDto;
import com.capstone.arfly.common.repository.FileRepository;
import com.capstone.arfly.community.domain.Post;
import com.capstone.arfly.community.domain.PostImage;
import com.capstone.arfly.community.repository.PostImageRepository;
import com.capstone.arfly.community.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostWriter {
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final FileRepository fileRepository;


    @Transactional
    public void savePostAndImages(Post newPost, List<FileDetailDto> fileDetailList){
        List<File> fileList = new ArrayList<>();
        List<PostImage> postImages = new ArrayList<>();

        for (int i = 0; i < fileDetailList.size(); i++) {
            FileDetailDto detail = fileDetailList.get(i);

            File fileEntity = File.builder()
                    .fileName(detail.getOriginalFileName())
                    .fileKey(detail.getKey())
                    .fileSize(detail.getFileSize())
                    .fileType(detail.getFileType())
                    .build();

            fileList.add(fileEntity);

            postImages.add(PostImage.builder()
                    .post(newPost)
                    .file(fileEntity)
                    .orderIndex(i)
                    .build());
        }
        postRepository.save(newPost);
        postImageRepository.saveAll(postImages);
        fileRepository.saveAll(fileList);
    }

    @Transactional
    public void savePost(Post newPost){
        postRepository.save(newPost);
    }
}
