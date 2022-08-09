/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Medicamentos;
import Entidades.Sitio;
import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author APRENDIZ
 */
public class SitioJpaController implements Serializable {

    public SitioJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Proyecto_FarmaciaPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sitio sitio) throws PreexistingEntityException, Exception {
        if (sitio.getMedicamentosList() == null) {
            sitio.setMedicamentosList(new ArrayList<Medicamentos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Medicamentos> attachedMedicamentosList = new ArrayList<Medicamentos>();
            for (Medicamentos medicamentosListMedicamentosToAttach : sitio.getMedicamentosList()) {
                medicamentosListMedicamentosToAttach = em.getReference(medicamentosListMedicamentosToAttach.getClass(), medicamentosListMedicamentosToAttach.getCodigo());
                attachedMedicamentosList.add(medicamentosListMedicamentosToAttach);
            }
            sitio.setMedicamentosList(attachedMedicamentosList);
            em.persist(sitio);
            for (Medicamentos medicamentosListMedicamentos : sitio.getMedicamentosList()) {
                Sitio oldSitioCodigoOfMedicamentosListMedicamentos = medicamentosListMedicamentos.getSitioCodigo();
                medicamentosListMedicamentos.setSitioCodigo(sitio);
                medicamentosListMedicamentos = em.merge(medicamentosListMedicamentos);
                if (oldSitioCodigoOfMedicamentosListMedicamentos != null) {
                    oldSitioCodigoOfMedicamentosListMedicamentos.getMedicamentosList().remove(medicamentosListMedicamentos);
                    oldSitioCodigoOfMedicamentosListMedicamentos = em.merge(oldSitioCodigoOfMedicamentosListMedicamentos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findSitio(sitio.getCodigo()) != null) {
                throw new PreexistingEntityException("Sitio " + sitio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sitio sitio) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sitio persistentSitio = em.find(Sitio.class, sitio.getCodigo());
            List<Medicamentos> medicamentosListOld = persistentSitio.getMedicamentosList();
            List<Medicamentos> medicamentosListNew = sitio.getMedicamentosList();
            List<String> illegalOrphanMessages = null;
            for (Medicamentos medicamentosListOldMedicamentos : medicamentosListOld) {
                if (!medicamentosListNew.contains(medicamentosListOldMedicamentos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Medicamentos " + medicamentosListOldMedicamentos + " since its sitioCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Medicamentos> attachedMedicamentosListNew = new ArrayList<Medicamentos>();
            for (Medicamentos medicamentosListNewMedicamentosToAttach : medicamentosListNew) {
                medicamentosListNewMedicamentosToAttach = em.getReference(medicamentosListNewMedicamentosToAttach.getClass(), medicamentosListNewMedicamentosToAttach.getCodigo());
                attachedMedicamentosListNew.add(medicamentosListNewMedicamentosToAttach);
            }
            medicamentosListNew = attachedMedicamentosListNew;
            sitio.setMedicamentosList(medicamentosListNew);
            sitio = em.merge(sitio);
            for (Medicamentos medicamentosListNewMedicamentos : medicamentosListNew) {
                if (!medicamentosListOld.contains(medicamentosListNewMedicamentos)) {
                    Sitio oldSitioCodigoOfMedicamentosListNewMedicamentos = medicamentosListNewMedicamentos.getSitioCodigo();
                    medicamentosListNewMedicamentos.setSitioCodigo(sitio);
                    medicamentosListNewMedicamentos = em.merge(medicamentosListNewMedicamentos);
                    if (oldSitioCodigoOfMedicamentosListNewMedicamentos != null && !oldSitioCodigoOfMedicamentosListNewMedicamentos.equals(sitio)) {
                        oldSitioCodigoOfMedicamentosListNewMedicamentos.getMedicamentosList().remove(medicamentosListNewMedicamentos);
                        oldSitioCodigoOfMedicamentosListNewMedicamentos = em.merge(oldSitioCodigoOfMedicamentosListNewMedicamentos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sitio.getCodigo();
                if (findSitio(id) == null) {
                    throw new NonexistentEntityException("The sitio with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sitio sitio;
            try {
                sitio = em.getReference(Sitio.class, id);
                sitio.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sitio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Medicamentos> medicamentosListOrphanCheck = sitio.getMedicamentosList();
            for (Medicamentos medicamentosListOrphanCheckMedicamentos : medicamentosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sitio (" + sitio + ") cannot be destroyed since the Medicamentos " + medicamentosListOrphanCheckMedicamentos + " in its medicamentosList field has a non-nullable sitioCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(sitio);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sitio> findSitioEntities() {
        return findSitioEntities(true, -1, -1);
    }

    public List<Sitio> findSitioEntities(int maxResults, int firstResult) {
        return findSitioEntities(false, maxResults, firstResult);
    }

    private List<Sitio> findSitioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sitio.class));
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

    public Sitio findSitio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sitio.class, id);
        } finally {
            em.close();
        }
    }

    public int getSitioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sitio> rt = cq.from(Sitio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
