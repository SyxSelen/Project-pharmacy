/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Entidades.Registrar;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author APRENDIZ
 */
public class RegistrarJpaController implements Serializable {

    public RegistrarJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Proyecto_FarmaciaPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Registrar registrar) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(registrar);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRegistrar(registrar.getCedula()) != null) {
                throw new PreexistingEntityException("Registrar " + registrar + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Registrar registrar) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            registrar = em.merge(registrar);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = registrar.getCedula();
                if (findRegistrar(id) == null) {
                    throw new NonexistentEntityException("The registrar with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Registrar registrar;
            try {
                registrar = em.getReference(Registrar.class, id);
                registrar.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The registrar with id " + id + " no longer exists.", enfe);
            }
            em.remove(registrar);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Registrar> findRegistrarEntities() {
        return findRegistrarEntities(true, -1, -1);
    }

    public List<Registrar> findRegistrarEntities(int maxResults, int firstResult) {
        return findRegistrarEntities(false, maxResults, firstResult);
    }

    private List<Registrar> findRegistrarEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Registrar.class));
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

    public Registrar findRegistrar(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Registrar.class, id);
        } finally {
            em.close();
        }
    }

    public int getRegistrarCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Registrar> rt = cq.from(Registrar.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
