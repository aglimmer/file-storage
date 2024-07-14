package com.developerspace.filestorageserver.domain.repository;


import com.developerspace.filestorageserver.domain.entity.FileObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileObjectRepository extends JpaRepository<FileObject, Long> {

}
