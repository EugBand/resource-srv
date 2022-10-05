package com.epam.epmcacm.msademo.resourcesrv.repository;

import com.epam.epmcacm.msademo.resourcesrv.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, String> {

}
