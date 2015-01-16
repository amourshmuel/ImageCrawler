/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.amour.imagecrawler.dal;

import com.amour.imagecrawler.dal.exceptions.NonexistentEntityException;
import com.amour.imagecrawler.dal.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author amour
 */
public class ImagesJpaController implements Serializable {

    public ImagesJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Images images) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(images);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findImages(images.getId()) != null) {
                throw new PreexistingEntityException("Images " + images + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Images images) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            images = em.merge(images);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = images.getId();
                if (findImages(id) == null) {
                    throw new NonexistentEntityException("The images with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Images images;
            try {
                images = em.getReference(Images.class, id);
                images.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The images with id " + id + " no longer exists.", enfe);
            }
            em.remove(images);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Images> findImagesEntities() {
        return findImagesEntities(true, -1, -1);
    }

    public List<Images> findImagesEntities(int maxResults, int firstResult) {
        return findImagesEntities(false, maxResults, firstResult);
    }

    private List<Images> findImagesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Images.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Images findImages(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Images.class, id);
        } finally {
            em.close();
        }
    }

    public int getImagesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Images> rt = cq.from(Images.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
