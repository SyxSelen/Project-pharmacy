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
import Entidades.Presentacion;
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
public class PresentacionJpaController implements Serializable {

    public PresentacionJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Proyecto_FarmaciaPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Presentacion presentacion) throws PreexistingEntityException, Exception {
        if (presentacion.getMedicamentosList() == null) {
            presentacion.setMedicamentosList(new ArrayList<Medicamentos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Medicamentos> attachedMedicamentosList = new ArrayList<Medicamentos>();
            for (Medicamentos medicamentosListMedicamentosToAttach : presentacion.getMedicamentosList()) {
                medicamentosListMedicamentosToAttach = em.getReference(medicamentosListMedicamentosToAttach.getClass(), medicamentosListMedicamentosToAttach.getCodigo());
                attachedMedicamentosList.add(medicamentosListMedicamentosToAttach);
            }
            presentacion.setMedicamentosList(attachedMedicamentosList);
            em.persist(presentacion);
            for (Medicamentos medicamentosListMedicamentos : presentacion.getMedicamentosList()) {
                Presentacion oldPresentacionCodigoOfMedicamentosListMedicamentos = medicamentosListMedicamentos.getPresentacionCodigo();
                medicamentosListMedicamentos.setPresentacionCodigo(presentacion);
                medicamentosListMedicamentos = em.merge(medicamentosListMedicamentos);
                if (oldPresentacionCodigoOfMedicamentosListMedicamentos != null) {
                    oldPresentacionCodigoOfMedicamentosListMedicamentos.getMedicamentosList().remove(medicamentosListMedicamentos);
                    oldPresentacionCodigoOfMedicamentosListMedicamentos = em.merge(oldPresentacionCodigoOfMedicamentosListMedicamentos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPresentacion(presentacion.getCodigo()) != null) {
                throw new PreexistingEntityException("Presentacion " + presentacion + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Presentacion presentacion) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Presentacion persistentPresentacion = em.find(Presentacion.class, presentacion.getCodigo());
            List<Medicamentos> medicamentosListOld = persistentPresentacion.getMedicamentosList();
            List<Medicamentos> medicamentosListNew = presentacion.getMedicamentosList();
            List<String> illegalOrphanMessages = null;
            for (Medicamentos medicamentosListOldMedicamentos : medicamentosListOld) {
                if (!medicamentosListNew.contains(medicamentosListOldMedicamentos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Medicamentos " + medicamentosListOldMedicamentos + " since its presentacionCodigo field is not nullable.");
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
            presentacion.setMedicamentosList(medicamentosListNew);
            presentacion = em.merge(presentacion);
            for (Medicamentos medicamentosListNewMedicamentos : medicamentosListNew) {
                if (!medicamentosListOld.contains(medicamentosListNewMedicamentos)) {
                    Presentacion oldPresentacionCodigoOfMedicamentosListNewMedicamentos = medicamentosListNewMedicamentos.getPresentacionCodigo();
                    medicamentosListNewMedicamentos.setPresentacionCodigo(presentacion);
                    medicamentosListNewMedicamentos = em.merge(medicamentosListNewMedicamentos);
                    if (oldPresentacionCodigoOfMedicamentosListNewMedicamentos != null && !oldPresentacionCodigoOfMedicamentosListNewMedicamentos.equals(presentacion)) {
                        oldPresentacionCodigoOfMedicamentosListNewMedicamentos.getMedicamentosList().remove(medicamentosListNewMedicamentos);
                        oldPresentacionCodigoOfMedicamentosListNewMedicamentos = em.merge(oldPresentacionCodigoOfMedicamentosListNewMedicamentos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = presentacion.getCodigo();
                if (findPresentacion(id) == null) {
                    throw new NonexistentEntityException("The presentacion with id " + id + " no longer exists.");
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
            Presentacion presentacion;
            try {
                presentacion = em.getReference(Presentacion.class, id);
                presentacion.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The presentacion with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Medicamentos> medicamentosListOrphanCheck = presentacion.getMedicamentosList();
            for (Medicamentos medicamentosListOrphanCheckMedicamentos : medicamentosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Presentacion (" + presentacion + ") cannot be destroyed since the Medicamentos " + medicamentosListOrphanCheckMedicamentos + " in its medicamentosList field has a non-nullable presentacionCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(presentacion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Presentacion> findPresentacionEntities() {
        return findPresentacionEntities(true, -1, -1);
    }

    public List<Presentacion> findPresentacionEntities(int maxResults, int firstResult) {
        return findPresentacionEntities(false, maxResults, firstResult);
    }

    private List<Presentacion> findPresentacionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Presentacion.class));
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

    public Presentacion findPresentacion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Presentacion.class, id);
        } finally {
            em.close();
        }
    }

    public int getPresentacionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Presentacion> rt = cq.from(Presentacion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
