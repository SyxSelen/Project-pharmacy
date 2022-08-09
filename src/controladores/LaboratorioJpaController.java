/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import Entidades.Laboratorio;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import Entidades.Medicamentos;
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
public class LaboratorioJpaController implements Serializable {

    public LaboratorioJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Proyecto_FarmaciaPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Laboratorio laboratorio) throws PreexistingEntityException, Exception {
        if (laboratorio.getMedicamentosList() == null) {
            laboratorio.setMedicamentosList(new ArrayList<Medicamentos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Medicamentos> attachedMedicamentosList = new ArrayList<Medicamentos>();
            for (Medicamentos medicamentosListMedicamentosToAttach : laboratorio.getMedicamentosList()) {
                medicamentosListMedicamentosToAttach = em.getReference(medicamentosListMedicamentosToAttach.getClass(), medicamentosListMedicamentosToAttach.getCodigo());
                attachedMedicamentosList.add(medicamentosListMedicamentosToAttach);
            }
            laboratorio.setMedicamentosList(attachedMedicamentosList);
            em.persist(laboratorio);
            for (Medicamentos medicamentosListMedicamentos : laboratorio.getMedicamentosList()) {
                Laboratorio oldLaboratorioCodigoOfMedicamentosListMedicamentos = medicamentosListMedicamentos.getLaboratorioCodigo();
                medicamentosListMedicamentos.setLaboratorioCodigo(laboratorio);
                medicamentosListMedicamentos = em.merge(medicamentosListMedicamentos);
                if (oldLaboratorioCodigoOfMedicamentosListMedicamentos != null) {
                    oldLaboratorioCodigoOfMedicamentosListMedicamentos.getMedicamentosList().remove(medicamentosListMedicamentos);
                    oldLaboratorioCodigoOfMedicamentosListMedicamentos = em.merge(oldLaboratorioCodigoOfMedicamentosListMedicamentos);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findLaboratorio(laboratorio.getCodigo()) != null) {
                throw new PreexistingEntityException("Laboratorio " + laboratorio + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Laboratorio laboratorio) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Laboratorio persistentLaboratorio = em.find(Laboratorio.class, laboratorio.getCodigo());
            List<Medicamentos> medicamentosListOld = persistentLaboratorio.getMedicamentosList();
            List<Medicamentos> medicamentosListNew = laboratorio.getMedicamentosList();
            List<String> illegalOrphanMessages = null;
            for (Medicamentos medicamentosListOldMedicamentos : medicamentosListOld) {
                if (!medicamentosListNew.contains(medicamentosListOldMedicamentos)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Medicamentos " + medicamentosListOldMedicamentos + " since its laboratorioCodigo field is not nullable.");
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
            laboratorio.setMedicamentosList(medicamentosListNew);
            laboratorio = em.merge(laboratorio);
            for (Medicamentos medicamentosListNewMedicamentos : medicamentosListNew) {
                if (!medicamentosListOld.contains(medicamentosListNewMedicamentos)) {
                    Laboratorio oldLaboratorioCodigoOfMedicamentosListNewMedicamentos = medicamentosListNewMedicamentos.getLaboratorioCodigo();
                    medicamentosListNewMedicamentos.setLaboratorioCodigo(laboratorio);
                    medicamentosListNewMedicamentos = em.merge(medicamentosListNewMedicamentos);
                    if (oldLaboratorioCodigoOfMedicamentosListNewMedicamentos != null && !oldLaboratorioCodigoOfMedicamentosListNewMedicamentos.equals(laboratorio)) {
                        oldLaboratorioCodigoOfMedicamentosListNewMedicamentos.getMedicamentosList().remove(medicamentosListNewMedicamentos);
                        oldLaboratorioCodigoOfMedicamentosListNewMedicamentos = em.merge(oldLaboratorioCodigoOfMedicamentosListNewMedicamentos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = laboratorio.getCodigo();
                if (findLaboratorio(id) == null) {
                    throw new NonexistentEntityException("The laboratorio with id " + id + " no longer exists.");
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
            Laboratorio laboratorio;
            try {
                laboratorio = em.getReference(Laboratorio.class, id);
                laboratorio.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The laboratorio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Medicamentos> medicamentosListOrphanCheck = laboratorio.getMedicamentosList();
            for (Medicamentos medicamentosListOrphanCheckMedicamentos : medicamentosListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Laboratorio (" + laboratorio + ") cannot be destroyed since the Medicamentos " + medicamentosListOrphanCheckMedicamentos + " in its medicamentosList field has a non-nullable laboratorioCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(laboratorio);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Laboratorio> findLaboratorioEntities() {
        return findLaboratorioEntities(true, -1, -1);
    }

    public List<Laboratorio> findLaboratorioEntities(int maxResults, int firstResult) {
        return findLaboratorioEntities(false, maxResults, firstResult);
    }

    private List<Laboratorio> findLaboratorioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Laboratorio.class));
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

    public Laboratorio findLaboratorio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Laboratorio.class, id);
        } finally {
            em.close();
        }
    }

    public int getLaboratorioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Laboratorio> rt = cq.from(Laboratorio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
