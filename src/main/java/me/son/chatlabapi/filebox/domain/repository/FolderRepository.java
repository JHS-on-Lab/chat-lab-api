package me.son.chatlabapi.filebox.domain.repository;

import me.son.chatlabapi.filebox.domain.entity.Folder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Folder> findByIdAndUser_Id(Long id, Long userId);
}
