package com.kb_hackathon.plovo.config;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.kb_hackathon.plovo.domain.User;
import com.kb_hackathon.plovo.domain.UserRecord;
import com.kb_hackathon.plovo.repository.UserRecordRepository;
import com.kb_hackathon.plovo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;
    private final UserRepository userRepository;
    private final UserRecordRepository userRecordRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public String upload(Long userId, MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
//        System.out.println("전환 완료");
        return upload(userId, uploadFile, dirName);
    }

    // s3로 파일 업로드하기
    private String upload(Long userId, File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        Optional<User> user = userRepository.findById(userId);
        user.get().setImage(uploadImageUrl);
        removeNewFile(uploadFile);

        return "유저 사진 저장 성공";
    }

    public String uploadRecord(Long userRecordId, MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File로 전환이 실패했습니다."));
        return uploadRecord(userRecordId, uploadFile, dirName);
    }

    // s3로 파일 업로드하기
    private String uploadRecord(Long userRecordId, File uploadFile, String dirName) {
        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드

        Optional<UserRecord> userRecord = userRecordRepository.findById(userRecordId);
        userRecord.get().setEnd_image(uploadImageUrl);
        userRecordRepository.save(userRecord.get());
        removeNewFile(uploadFile);

        return "플로깅 종료 사진 등록 완료";
    }

    // 업로드하기
    private String putS3(File uploadFile, String fileName){
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        System.out.println(convertFile);
//        System.out.println("convert");
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
//            System.out.println("생성 완료");  // 여기서 경로 이상한지 출력X
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
