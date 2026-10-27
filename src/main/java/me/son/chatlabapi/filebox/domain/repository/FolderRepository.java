package me.son.chatlabapi.filebox.domain.repository;

import me.son.chatlabapi.filebox.domain.entity.Folder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<Folder> findByIdAndUser_Id(Long id, Long userId);

    // 자기참조 FK 때문에 한 번에 지우면 순서에 따라 제약 위반이 날 수 있어 부모 참조를 먼저 끊는다
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Folder f SET f.parentFolder = null WHERE f.user.id = :userId")
    void detachParentsByUserId(@Param("userId") Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Folder f WHERE f.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
