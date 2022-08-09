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
import Entidades.Laboratorio;
import Entidades.Medicamentos;
import Entidades.Presentacion;
import Entidades.Sitio;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author APRENDIZ
 */
public class MedicamentosJpaController implements Serializable {

    public MedicamentosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("Proyecto_FarmaciaPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Medicamentos medicamentos) throws PreexistingEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Laboratorio laboratorioCodigo = medicamentos.getLaboratorioCodigo();
            if (laboratorioCodigo != null) {
                laboratorioCodigo = em.getReference(laboratorioCodigo.getClass(), laboratorioCodigo.getCodigo());
                medicamentos.setLaboratorioCodigo(laboratorioCodigo);
            }
            Presentacion presentacionCodigo = medicamentos.getPresentacionCodigo();
            if (presentacionCodigo != null) {
                presentacionCodigo = em.getReference(presentacionCodigo.getClass(), presentacionCodigo.getCodigo());
                medicamentos.setPresentacionCodigo(presentacionCodigo);
            }
            Sitio sitioCodigo = medicamentos.getSitioCodigo();
            if (sitioCodigo != null) {
                sitioCodigo = em.getReference(sitioCodigo.getClass(), sitioCodigo.getCodigo());
                medicamentos.setSitioCodigo(sitioCodigo);
            }
            em.persist(medicamentos);
            if (laboratorioCodigo != null) {
                laboratorioCodigo.getMedicamentosList().add(medicamentos);
                laboratorioCodigo = em.merge(laboratorioCodigo);
            }
            if (presentacionCodigo != null) {
                presentacionCodigo.getMedicamentosList().add(medicamentos);
                presentacionCodigo = em.merge(presentacionCodigo);
            }
            if (sitioCodigo != null) {
                sitioCodigo.getMedicamentosList().add(medicamentos);
                sitioCodigo = em.merge(sitioCodigo);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMedicamentos(medicamentos.getCodigo()) != null) {
                throw new PreexistingEntityException("Medicamentos " + medicamentos + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Medicamentos medicamentos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Medicamentos persistentMedicamentos = em.find(Medicamentos.class, medicamentos.getCodigo());
            Laboratorio laboratorioCodigoOld = persistentMedicamentos.getLaboratorioCodigo();
            Laboratorio laboratorioCodigoNew = medicamentos.getLaboratorioCodigo();
            Presentacion presentacionCodigoOld = persistentMedicamentos.getPresentacionCodigo();
            Presentacion presentacionCodigoNew = medicamentos.getPresentacionCodigo();
            Sitio sitioCodigoOld = persistentMedicamentos.getSitioCodigo();
            Sitio sitioCodigoNew = medicamentos.getSitioCodigo();
            if (laboratorioCodigoNew != null) {
                laboratorioCodigoNew = em.getReference(laboratorioCodigoNew.getClass(), laboratorioCodigoNew.getCodigo());
                medicamentos.setLaboratorioCodigo(laboratorioCodigoNew);
            }
            if (presentacionCodigoNew != null) {
                presentacionCodigoNew = em.getReference(presentacionCodigoNew.getClass(), presentacionCodigoNew.getCodigo());
                medicamentos.setPresentacionCodigo(presentacionCodigoNew);
            }
            if (sitioCodigoNew != null) {
                sitioCodigoNew = em.getReference(sitioCodigoNew.getClass(), sitioCodigoNew.getCodigo());
                medicamentos.setSitioCodigo(sitioCodigoNew);
            }
            medicamentos = em.merge(medicamentos);
            if (laboratorioCodigoOld != null && !laboratorioCodigoOld.equals(laboratorioCodigoNew)) {
                laboratorioCodigoOld.getMedicamentosList().remove(medicamentos);
                laboratorioCodigoOld = em.merge(laboratorioCodigoOld);
            }
            if (laboratorioCodigoNew != null && !laboratorioCodigoNew.equals(laboratorioCodigoOld)) {
                laboratorioCodigoNew.getMedicamentosList().add(medicamentos);
                laboratorioCodigoNew = em.merge(laboratorioCodigoNew);
            }
            if (presentacionCodigoOld != null && !presentacionCodigoOld.equals(presentacionCodigoNew)) {
                presentacionCodigoOld.getMedicamentosList().remove(medicamentos);
                presentacionCodigoOld = em.merge(presentacionCodigoOld);
            }
            if (presentacionCodigoNew != null && !presentacionCodigoNew.equals(presentacionCodigoOld)) {
                presentacionCodigoNew.getMedicamentosList().add(medicamentos);
                presentacionCodigoNew = em.merge(presentacionCodigoNew);
            }
            if (sitioCodigoOld != null && !sitioCodigoOld.equals(sitioCodigoNew)) {
                sitioCodigoOld.getMedicamentosList().remove(medicamentos);
                sitioCodigoOld = em.merge(sitioCodigoOld);
            }
            if (sitioCodigoNew != null && !sitioCodigoNew.equals(sitioCodigoOld)) {
                sitioCodigoNew.getMedicamentosList().add(medicamentos);
                sitioCodigoNew = em.merge(sitioCodigoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = medicamentos.getCodigo();
                if (findMedicamentos(id) == null) {
                    throw new NonexistentEntityException("The medicamentos with id " + id + " no longer exists.");
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
            Medicamentos medicamentos;
            try {
                medicamentos = em.getReference(Medicamentos.class, id);
                medicamentos.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The medicamentos with id " + id + " no longer exists.", enfe);
            }
            Laboratorio laboratorioCodigo = medicamentos.getLaboratorioCodigo();
            if (laboratorioCodigo != null) {
                laboratorioCodigo.getMedicamentosList().remove(medicamentos);
                laboratorioCodigo = em.merge(laboratorioCodigo);
            }
            Presentacion presentacionCodigo = medicamentos.getPresentacionCodigo();
            if (presentacionCodigo != null) {
                presentacionCodigo.getMedicamentosList().remove(medicamentos);
                presentacionCodigo = em.merge(presentacionCodigo);
            }
            Sitio sitioCodigo = medicamentos.getSitioCodigo();
            if (sitioCodigo != null) {
                sitioCodigo.getMedicamentosList().remove(medicamentos);
                sitioCodigo = em.merge(sitioCodigo);
            }
            em.remove(medicamentos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Medicamentos> findMedicamentosEntities() {
        return findMedicamentosEntities(true, -1, -1);
    }

    public List<Medicamentos> findMedicamentosEntities(int maxResults, int firstResult) {
        return findMedicamentosEntities(false, maxResults, firstResult);
    }

    private List<Medicamentos> findMedicamentosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Medicamentos.class));
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

    public Medicamentos findMedicamentos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Medicamentos.class, id);
        } finally {
            em.close();
        }
    }

    public int getMedicamentosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Medicamentos> rt = cq.from(Medicamentos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
