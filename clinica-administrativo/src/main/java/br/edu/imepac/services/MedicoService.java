package br.edu.imepac.services;

import br.edu.imepac.dtos.Medico.MedicoDtoRequest;
import br.edu.imepac.dtos.Medico.MedicoDtoResponse;
import br.edu.imepac.models.EspecialidadeModel;
import br.edu.imepac.models.MedicoModel;
import br.edu.imepac.repositories.EspecialidadeRepository;
import br.edu.imepac.repositories.MedicoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MedicoService {
    private MedicoRepository medicoRepository;

    private EspecialidadeRepository especialidadeRepository;

    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(MedicoService.class);

    public MedicoService(MedicoRepository medicoRepository, ModelMapper modelMapper, EspecialidadeRepository especialidadeRepository) {
        this.modelMapper = modelMapper;
        this.medicoRepository = medicoRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    public ResponseEntity<String> delete(Long id) {
        logger.info("Medico Service deleting Medico");
        medicoRepository.deleteById(id);
        return ResponseEntity.ok().body("{\"mensagem\": \"Excluido com sucesso!\"}");
    }

    public List<MedicoDtoResponse> findAll() {
        logger.info("Medico Service find All Medicos");
        List<MedicoModel> medicos = medicoRepository.findAll();
        List<MedicoDtoResponse> dto = medicos.stream()
                .map(medico -> modelMapper.map(medico, MedicoDtoResponse.class))
                .collect(Collectors.toList());
        return dto;
    }

    public MedicoDtoResponse update(Long id, MedicoDtoRequest medicoDetails) {
        logger.info("Medico Service updating Medico");
        Optional<MedicoModel> optionalMedico = medicoRepository.findById(id);

        if (optionalMedico.isPresent()) {
            MedicoModel medicoModel = optionalMedico.get();
            Optional.ofNullable(medicoDetails.getNome()).ifPresent(medicoModel::setNome);
            Optional.ofNullable(medicoDetails.getCrm()).ifPresent(medicoModel::setCrm);
            Optional.ofNullable(medicoDetails.getSenha()).ifPresent(medicoModel::setSenha);
            if(medicoDetails.getEspecialidade() != null){
                Optional<EspecialidadeModel> optionalEspecialidade = especialidadeRepository.findById(medicoDetails.getEspecialidade());
                optionalEspecialidade.ifPresent(medicoModel::setEspecialidade);
            }
            logger.info("Medico Service update Medico");
            MedicoModel updatedMedico = medicoRepository.save(medicoModel);
            logger.info("Updated medico");
            MedicoDtoResponse medicoDto = modelMapper.map(updatedMedico, MedicoDtoResponse.class);
            return medicoDto;
        } else {
            logger.error("Medico Service Update. Medico not found");
            return null;
        }
    }

    public MedicoDtoResponse save(MedicoDtoRequest medicoRequest) {
        MedicoModel medicoModel = modelMapper.map(medicoRequest, MedicoModel.class);
        Optional<EspecialidadeModel> optionalEspecialidade = especialidadeRepository.findById(medicoRequest.getEspecialidade());
        if(optionalEspecialidade.isPresent()){
            logger.info("Medico Service Save Medico");
            EspecialidadeModel especialidade = optionalEspecialidade.get();
            medicoModel.setEspecialidade(especialidade);
            MedicoModel savedMedico = medicoRepository.save(medicoModel);
            MedicoDtoResponse medicoDto = modelMapper.map(savedMedico, MedicoDtoResponse.class);
            medicoDto.setEspecialidadeNome(savedMedico.getEspecialidade().getNome());
            return medicoDto;
        } else {
            logger.error("Medico Service Error Creating Medico");
            return null;
        }
        
    }

    public MedicoDtoResponse findById(Long id) {
        logger.info("Medico Service find by Id Medico");
        Optional<MedicoModel> optionalMedico = medicoRepository.findById(id);
        if (optionalMedico.isPresent()) {
            MedicoModel medicoModel = optionalMedico.get(); 
            MedicoDtoResponse medicoDto = modelMapper.map(medicoModel, MedicoDtoResponse.class);
            return medicoDto;
        } else {
            logger.error("Medico Service findById. Medico not found");
            return null;
        }
    }
}
