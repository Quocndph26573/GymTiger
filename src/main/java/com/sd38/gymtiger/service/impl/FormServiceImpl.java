package com.sd38.gymtiger.service.impl;

import com.sd38.gymtiger.repository.FormRepository;
import com.sd38.gymtiger.model.Form;
import com.sd38.gymtiger.service.FormService;
import com.sd38.gymtiger.utils.FileUtil;
import com.sd38.gymtiger.utils.attribute.FormExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FormServiceImpl implements FormService {
    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormExcelUtil excelUtil;

    @Override
    public List<Form> getAll(){
        return formRepository.findAllByStatusOrderByUpdateDateDesc(1);
    }

    @Override
    public Page<Form> getPage(Integer page){
        Pageable pageable = PageRequest.of(page, 10);
        return formRepository.findAllByStatusOrderByUpdateDateDesc(pageable, 1);
    }

    private Boolean checkName(String name) {
        // Loại bỏ dấu cách đầu tiên
        name = name.replaceFirst("^\\s+", "");

        // Loại bỏ các dấu cách khi có hai dấu cách trở lên liền nhau
        name = name.replaceAll("\\s{2,}", " ");
        Form form = formRepository.findByNameAndStatus(name, 1);
        if (form != null) {
            return false;
        }
        return true;
    }

    public String formatName(String name) {
        // Loại bỏ dấu cách đầu tiên
        name = name.replaceFirst("^\\s+", "");

        // Loại bỏ các dấu cách khi có hai dấu cách trở lên liền nhau
        name = name.replaceAll("\\s{2,}", " ");
        return name;
    }

    public String generateCode() {
        Form formFinalPresent = formRepository.findTopByOrderByIdDesc();
        if (formFinalPresent == null) {
            return "KD00001";
        }
        Integer idFinalPresent = formFinalPresent.getId() + 1;
        String code = String.format("%04d", idFinalPresent);
        return "KD"+code;
    }

    @Override
    public Boolean add(String name) {
        if (checkName(name)) {
            Form form = new Form();
            form.setName(formatName(name));
            form.setStatus(1);
            form.setCreateDate(new java.util.Date());
            form.setUpdateDate(new java.util.Date());
            form.setCode(generateCode());
            formRepository.save(form);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean update(Form form, Integer id) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form updateForm = optional.get();
            if (updateForm.getName().equalsIgnoreCase(formatName(form.getName()))) {
                updateForm.setId(id);
                updateForm.setName(formatName(form.getName()));
                updateForm.setUpdateDate(new Date());
                formRepository.save(updateForm);
                return true;
            } else {
                if (checkName(form.getName())) {
                    updateForm.setId(id);
                    updateForm.setName(formatName(form.getName()));
                    updateForm.setUpdateDate(new Date());
                    formRepository.save(updateForm);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public Form delete(Integer id) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            Form form = optional.get();
            form.setStatus(0);
            form.setUpdateDate(new Date());
            return formRepository.save(form);
        } else {
            return null;
        }
    }

    @Override
    public Page<Form> search(String name, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return formRepository.findAllByNameContainsAndStatusOrderByIdDesc(name.trim(), 1, pageable);
    }

    @Override
    public Form detail(Integer id) {
        Optional<Form> optional = formRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    @Override
    public Page<Form> getAllDeleted(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return formRepository.findAllByStatusOrderByUpdateDateDesc(pageable, 0);
    }

    @Override
    public Form restore(Integer id) {
        Optional<Form> optionalForm = formRepository.findById(id);
        if (optionalForm.isPresent()) {
            Form restoreForm = optionalForm.get();
            restoreForm.setStatus(1);
            restoreForm.setUpdateDate(new Date());
            return formRepository.save(restoreForm);
        } else {
            return null;
        }
    }

    @Override
    public Page<Form> searchDelete(String name, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return formRepository.findAllByNameContainsAndStatusOrderByIdDesc(name.trim(), 0, pageable);
    }

    @Override
    public String importExcel(MultipartFile file) throws IOException {
        if (excelUtil.isValidExcel(file)) {
            String uploadDir = "./src/main/resources/static/filecustom/size/";
            String fileName = file.getOriginalFilename();
            String excelPath = FileUtil.copyFile(file, fileName, uploadDir);
            String status = excelUtil.getFromExcel(excelPath);
            if (status.contains("Sai dữ liệu")) {
                return "Sai dữ liệu";
            } else if (status.contains("Trùng")) {
                return "Trùng";
            } else if (status.contains("Tồn tại")){
                return "Tồn tại";
            } else if (status.contains("Trống")) {
                return "Trống";
            } else {
                return "Oke";
            }
        } else {
            return "Lỗi file"; // Lỗi file
        }
    }
}
