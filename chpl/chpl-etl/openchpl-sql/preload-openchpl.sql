insert into openchpl.practice_type (name, description, last_modified_user) values ('Ambulatory','Ambulatory',-1), ('Inpatient','Inpatient',-1);
insert into openchpl.product_classification_type (name, description, last_modified_user) values ('Modular EHR','Modular EHR',-1), ('Complete EHR','Complete EHR',-1);
insert into openchpl.cqm_edition (edition, last_modified_user) values (2011, -1),(2014,-1);
insert into openchpl.certification_edition (year, last_modified_user) values (2011, -1),(2014,-1);
insert into openchpl.cqm_criterion_type (name, description, last_modified_user) values ('Ambulatory', 'Ambulatory', -1),('Inpatient','Inpatient',-1);
insert into openchpl.certification_body (name, last_modified_user) values ('InfoGard', -1), ('CCHIT', -1), ('Drummond Group Inc.', -1), ('SLI Global', -1), ('Surescripts LLC', -1), ('ICSA Labs', -1);

--alter table openchpl.event_type alter column event_type_id type bigserial;
insert into openchpl.event_type (event_type_id, name, description, last_modified_user) values (1,'Certification','Product is certified', -1);

alter table openchpl.additional_software alter column name type varchar(500);
alter table openchpl.certified_product add column atcb_certification_id varchar(250);

INSERT INTO openchpl.certification_criterion (certification_edition_id, number, title, last_modified_user) VALUES
(1, '170.302(a)', 'Drug-drug, drug-allergy', -1),
(1, '170.302(b)', 'Drug formulary checks', -1),
(1, '170.302(c)', 'Maintain up-to-date prob', -1),
(1, '170.302(d)', 'Maintain active med list', -1),
(1, '170.302(e)', 'Maintain active allergy list', -1),
(1, '170.302(f)', 'Record and Chart Vital Signs', -1),
(1, '170.302(f)(1)', 'Record and Chart Vital', -1),
(1, '170.302(f)(2)', 'Calculate BMI', -1),
(1, '170.302(f)(3)', 'Plot and display growth', -1),
(1, '170.302(g)', 'Smoking status', -1),
(1, '170.302(h)', 'Incorporate lab test results', -1),
(1, '170.302(i)', 'Generate patient lists', -1),
(1, '170.302(j)', 'Medication Reconciliation', -1),
(1, '170.302(k)', 'Submission to immun', -1),
(1, '170.302(l)', 'Public Health Surveillance', -1),
(1, '170.302(m)', 'Patient Specific Education', -1),
(1, '170.302(n)', 'Automated measure calc', -1),
(1, '170.302(o)', 'Access Control', -1),
(1, '170.302(p)', 'Emergency Access', -1),
(1, '170.302(q)', 'Automatic log-off', -1),
(1, '170.302(r)', 'Audit Log', -1),
(1, '170.302(s)', 'Integrity', -1),
(1, '170.302(t)', 'Authentication', -1),
(1, '170.302(u)', 'General Encryption', -1),
(1, '170.302(v)', 'Encryption when exchanging', -1),
(1, '170.302(w)', 'Accounting of disclosures', -1),
(1, '170.304(a)', 'Computerized provider OE', -1),
(1, '170.304(b)', 'Electronic Prescribing', -1),
(1, '170.304(c)', 'Record Demographics', -1),
(1, '170.304(d)', 'Patient Reminders', -1),
(1, '170.304(e)', 'Clinical Decision Support', -1),
(1, '170.304(f)', 'Electronic Copy of Health', -1),
(1, '170.304(g)', 'Timely Access', -1),
(1, '170.304(h)', 'Clinical Summaries', -1),
(1, '170.304(i)', 'Exchange Clinical Info', -1),
(1, '170.304(j)', 'Calculate and Submit Clinical', -1),
(1, '170.306(a)', 'Computerized Provider OE', -1),
(1, '170.306(b)', 'Record Demographics', -1),
(1, '170.306(c)', 'Clinical Decision Support', -1),
(1, '170.306(d)', 'Electronic copy of Health Inf', -1),
(1, '170.306(d)(1)', 'Electronic copy of health', -1),
(1, '170.306(d)(2)', 'E-copy of health info', -1),
(1, '170.306(e)', 'Electronic copy of discharge', -1),
(1, '170.306(f)', 'Exchange Clinical Info', -1),
(1, '170.306(g)', 'Reportable Lab Results', -1),
(1, '170.306(h)', 'Advance Directives', -1),
(1, '170.306(i)', 'Calculate and Submit Clinical', -1),
(2, '170.314(a)(1)', 'Computerized provider OE', -1),
(2, '170.314(a)(2)', 'Drug-drug, drug-allergy', -1),
(2, '170.314(a)(3)', 'Demographics', -1),
(2, '170.314(a)(4)', 'Vital signs, body mass ind', -1),
(2, '170.314(a)(5)', 'Problem List', -1),
(2, '170.314(a)(6)', 'Medication List', -1),
(2, '170.314(a)(7)', 'Medication Allergy List', -1),
(2, '170.314(a)(8)', 'Clinical Decision Support', -1),
(2, '170.314(a)(9)', 'Electronic Notes', -1),
(2, '170.314(a)(20)', 'Drug-Formulary Checks', -1),
(2, '170.314(a)(21)', 'Smoking Status', -1),
(2, '170.314(a)(22)', 'Image Results', -1),
(2, '170.314(a)(23)', 'Family Health History', -1),
(2, '170.314(a)(24)', 'Patient List Creation', -1),
(2, '170.314(a)(25)', 'Patient-Specific Educatio', -1),
(2, '170.314(a)(26)', 'Electronic Medication Adm', -1),
(2, '170.314(a)(27)', 'Advance Directives', -1),
(2, '170.314(b)(1)', 'Transitions of Care - rece', -1),
(2, '170.314(b)(2)', 'Transitions of Care - crea', -1),
(2, '170.314(b)(3)', 'Electronic Prescribing', -1),
(2, '170.314(b)(4)', 'Clinical Information Recon', -1),
(2, '170.314(b)(5)', 'Incorporate Laboratory Tes', -1),
(2, '170.314(b)(6)', 'Transmission of Electronic', -1),
(2, '170.314(b)(7)', 'Data Portability', -1),
(2, '170.314(c)(1)', 'Clinical Quality Measures', -1),
(2, '170.314(c)(2)', 'Clinical Quality Measures', -1),
(2, '170.314(c)(3)', 'Clinical Quality Measures', -1),
(2, '170.314(d)(1)', 'Authentication, access con', -1),
(2, '170.314(d)(2)', 'Auditable Events and Tampe', -1),
(2, '170.314(d)(3)', 'Audit Report(s)', -1),
(2, '170.314(d)(4)', 'Amendments', -1),
(2, '170.314(d)(5)', 'Automatic log-off', -1),
(2, '170.314(d)(6)', 'Emergency access', -1),
(2, '170.314(d)(7)', 'End-User Device Encryption', -1),
(2, '170.314(d)(8)', 'Integrity', -1),
(2, '170.314(d)(9)', 'Accounting of Disclosures', -1),
(2, '170.314(e)(1)', 'View, Download, and Transm', -1),
(2, '170.314(e)(2)', 'Clinical Summary', -1),
(2, '170.314(e)(3)', 'Secure Messaging', -1),
(2, '170.314(f)(1)', 'Immunization Information', -1),
(2, '170.314(f)(2)', 'Transmission to Immunizati', -1),
(2, '170.314(f)(3)', 'Transmission to Public Hea', -1),
(2, '170.314(f)(4)', 'Transmission of Reportable', -1),
(2, '170.314(f)(5)', 'Cancer Case Information', -1),
(2, '170.314(f)(6)', 'Transmission to Cancer Reg', -1),
(2, '170.314(g)(1)', 'Automated Numerator Record', -1),
(2, '170.314(g)(2)', 'Automated Measure Calculat', -1),
(2, '170.314(g)(3)', 'Safety-Enhanced Design', -1),
(2, '170.314(g)(4)', 'Quality Management System', -1);

INSERT INTO openchpl.cqm_criterion (cqm_edition_id, cqm_criterion_type_id, number, title, last_modified_user) VALUES
(1, 1, 'NQF 0001(A)', 'Asthma Assessment', -1),
(1, 1, 'NQF 0002(A)', 'Pharyngitis Testing', -1),
(1, 1, 'NQF 0004(A)', 'Alcohol and Other Drug Depe', -1),
(1, 1, 'NQF 0012(A)', 'Prenatal Care: HIV', -1),
(1, 1, 'NQF 0013(A)', 'Hypertension: Blood Pressure', -1),
(1, 1, 'NQF 0014(A)', 'Prenatal Care: Immune Globul', -1),
(1, 1, 'NQF 0018(A)', 'Controlling High Blood Pres', -1),
(1, 1, 'NQF 0024(A)', 'Weight Assessment for Child', -1),
(1, 1, 'NQF 0027(A)', 'Smoking and Tobacco Use', -1),
(1, 1, 'NQF 0028(A)', 'Preventive Care & Screening', -1),
(1, 1, 'NQF 0031(A)', 'Breast Cancer Screening', -1),
(1, 1, 'NQF 0032(A)', 'Cervical Cancer Screening', -1),
(1, 1, 'NQF 0033(A)', 'Chlamydia Screening for Wome', -1),
(1, 1, 'NQF 0034(A)', 'Colorectal Cancer Screening', -1),
(1, 1, 'NQF 0036(A)', 'Use of Medication for Asthma', -1),
(1, 1, 'NQF 0038(A)', 'Child Immunization Status', -1),
(1, 1, 'NQF 0041(A)', 'Preventive Care & Screening', -1),
(1, 1, 'NQF 0043(A)', 'Pneomonia Vaccination Status', -1),
(1, 1, 'NQF 0047(A)', 'Asthma Pharmacologic Therapy', -1),
(1, 1, 'NQF 0052(A)', 'Low Back Pain: Imaging', -1),
(1, 1, 'NQF 0055(A)', 'Diabetes: Eye Exam', -1),
(1, 1, 'NQF 0056(A)', 'Diabetes: Foot Exam', -1),
(1, 1, 'NQF 0059(A)', 'Diabetes: Hemoglobin A1c', -1),
(1, 1, 'NQF 0061(A)', 'Diabetes: BP Management', -1),
(1, 1, 'NQF 0062(A)', 'Diabates: Urine Screening', -1),
(1, 1, 'NQF 0064(A)', 'Diabetes: LDL', -1),
(1, 1, 'NQF 0067(A)', 'Coronary Artery Disease', -1),
(1, 1, 'NQF 0068(A)', 'Vascular Disease: Aspirin', -1),
(1, 1, 'NQF 0070(A)', 'CAD: Beta-Blocker Therapy', -1),
(1, 1, 'NQF 0073(A)', 'Vascular Disease: BP Mgmt', -1),
(1, 1, 'NQF 0074(A)', 'Coronary Artery Disease', -1),
(1, 1, 'NQF 0075(A)', 'Vascular Disease: LDL', -1),
(1, 1, 'NQF 0081(A)', 'Heart Failure: ACE Inhibitor', -1),
(1, 1, 'NQF 0083(A)', 'Heart Failure: LVSD Therapy', -1),
(1, 1, 'NQF 0084(A)', 'Heart Failure: Warfarin', -1),
(1, 1, 'NQF 0086(A)', 'Primary Open Angle Glaucoma', -1),
(1, 1, 'NQF 0088(A)', 'Diabetic Retinopathy: Docs', -1),
(1, 1, 'NQF 0089(A)', 'Diabetic Retinopathy: Com', -1),
(1, 1, 'NQF 0105(A)', 'Anti-depressant Med Mgmt', -1),
(1, 1, 'NQF 0371(I)', 'VTE: Prophylaxis', -1),
(1, 1, 'NQF 0372(I)', 'VTE: Intensive Care Unit', -1),
(1, 1, 'NQF 0373(I)', 'VTE: Anticoagulation Overlap', -1),
(1, 1, 'NQF 0374(I)', 'VTE: Platelet Monitoring', -1),
(1, 1, 'NQF 0375(I)', 'VTE: Discharge Instructions', -1),
(1, 1, 'NQF 0376(I)', 'VTE: Incidence of Preventabl', -1),
(1, 1, 'NQF 0385(A)', 'Oncology Colon Cancer', -1),
(1, 1, 'NQF 0387(A)', 'Oncology Brest Cancer', -1),
(1, 1, 'NQF 0389(A)', 'Prostate Cancer: Bone Scan', -1),
(1, 1, 'NQF 0421(A)', 'Adult Weight Screening', -1),
(1, 1, 'NQF 0435(I)', 'Stroke: Discharge', -1),
(1, 1, 'NQF 0436(I)', 'Stroke: Anticoagulation', -1),
(1, 1, 'NQF 0437(I)', 'Stroke: Thrombolytic', -1),
(1, 1, 'NQF 0438(I)', 'Stroke: Ischemic/Hemorrhagic', -1),
(1, 1, 'NQF 0439(I)', 'Stroke: Discharge on Statins', -1),
(1, 1, 'NQF 0440(I)', 'Stroke: Stroke Education', -1),
(1, 1, 'NQF 0441(I)', 'Stroke: Rehabilitation', -1),
(1, 1, 'NQF 0495(I)', 'ED: Median Time', -1),
(1, 1, 'NQF 0497(I)', 'ED: Admission Decision', -1),
(1, 1, 'NQF 0575(A)', 'Diabetes: Hemoglobin A1c', -1),
(2, 1, 'CMS100', 'AMI-2-Aspirin Prescribed at Dis', -1),
(2, 1, 'CMS102', 'Stroke-10 Ischemic or hemorrhag', -1),
(2, 1, 'CMS104', 'Stroke-2 Ischemic stroke - Dis', -1),
(2, 1, 'CMS105', 'Stroke-6 Ischemic stroke - Disc', -1),
(2, 1, 'CMS107', 'Stroke-8 Ischemic or hemorrhagi', -1),
(2, 1, 'CMS108', 'Venous Thromboembolism (VTE)-1', -1),
(2, 1, 'CMS109', 'VTE-4 VTE Patients Receiving Un', -1),
(2, 1, 'CMS110', 'VTE-5 VTE discharge instruction', -1),
(2, 1, 'CMS111', 'ED-2 Emergency Department Throu', -1),
(2, 1, 'CMS113', 'PC-01 Elective Delivery Prior t', -1),
(2, 1, 'CMS114', 'VTE-6 Incidence of potentially', -1),
(2, 1, 'CMS117', 'Childhood Immunization Status', -1),
(2, 1, 'CMS122', 'Diabetes: Hemoglobin A1c Poor', -1),
(2, 1, 'CMS123', 'Diabetes: Foot Exam', -1),
(2, 1, 'CMS124', 'Cervical Cancer Screening', -1),
(2, 1, 'CMS125', 'Breast Cancer Screening', -1),
(2, 1, 'CMS126', 'Use of Appropriate Medications', -1),
(2, 1, 'CMS127', 'Pneumonia Vaccination Status fo', -1),
(2, 1, 'CMS128', 'Anti-depressant Medication Mana', -1),
(2, 1, 'CMS129', 'Prostate Cancer: Avoidance of', -1),
(2, 1, 'CMS130', 'Colorectal Cancer Screening', -1),
(2, 1, 'CMS131', 'Diabetes: Eye Exam', -1),
(2, 1, 'CMS132', 'Cataracts: Complications withi', -1),
(2, 1, 'CMS133', 'Cataracts: 20/40 or Better Vis', -1),
(2, 1, 'CMS134', 'Diabetes: Urine Protein Screen', -1),
(2, 1, 'CMS135', 'Heart Failure (HF): Angiotensi', -1),
(2, 1, 'CMS136', 'ADHD: Follow-Up Care for Child', -1),
(2, 1, 'CMS137', 'Initiation and Engagement of Al', -1),
(2, 1, 'CMS138', 'Preventive Care and Screening:', -1),
(2, 1, 'CMS139', 'Screening for Future Fall Risk', -1),
(2, 1, 'CMS140', 'Breast Cancer: Hormonal Therapy', -1),
(2, 1, 'CMS141', 'Colon Cancer: Chemotherapy for', -1),
(2, 1, 'CMS142', 'Diabetic Retinopathy: Communic', -1),
(2, 1, 'CMS143', 'Primary Open Angle Glaucoma (PO', -1),
(2, 1, 'CMS144', 'Heart Failure (HF): Beta-Block', -1),
(2, 1, 'CMS145', 'Coronary Artery Disease (CAD):', -1),
(2, 1, 'CMS146', 'Appropriate Testing for Childre', -1),
(2, 1, 'CMS147', 'Preventative Care and Screening', -1),
(2, 1, 'CMS148', 'Hemoglobin A1c Test for Pediatr', -1),
(2, 1, 'CMS149', 'Dementia: Cognitive Assessment', -1),
(2, 1, 'CMS153', 'Chlamydia Screening for Women', -1),
(2, 1, 'CMS154', 'Appropriate Treatment for Child', -1),
(2, 1, 'CMS155', 'Weight Assessment and Counselin', -1),
(2, 1, 'CMS156', 'Use of High-Risk Medications in', -1),
(2, 1, 'CMS157', 'Oncology: Medical and Radiatio', -1),
(2, 1, 'CMS158', 'Pregnant women that had HBsAg t', -1),
(2, 1, 'CMS159', 'Depression Remission at Twelve', -1),
(2, 1, 'CMS160', 'Depression Utilization of the P', -1),
(2, 1, 'CMS161', 'Major Depressive Disorder (MDD)', -1),
(2, 1, 'CMS163', 'Diabetes: Low Density Lipoprot', -1),
(2, 1, 'CMS164', 'Ischemic Vascular Disease (IVD)', -1),
(2, 1, 'CMS165', 'Controlling High Blood Pressure', -1),
(2, 1, 'CMS166', 'Use of Imaging Studies for Low', -1),
(2, 1, 'CMS167', 'Diabetic Retinopathy: Document', -1),
(2, 1, 'CMS169', 'Bipolar Disorder and Major Depr', -1),
(2, 1, 'CMS171', 'SCIP-INF-1 Prophylactic Antibio', -1),
(2, 1, 'CMS172', 'SCIP-INF-2-Prophylactic Antibio', -1),
(2, 1, 'CMS177', 'Child and Adolescent Major Depr', -1),
(2, 1, 'CMS178', 'SCIP-INF-9- Urinary catheter re', -1),
(2, 1, 'CMS179', 'ADE Prevention and Monitoring:', -1),
(2, 1, 'CMS182', 'Ischemic Vascular Disease (IVD)', -1),
(2, 1, 'CMS185', 'Healthy Term Newborn', -1),
(2, 1, 'CMS188', 'PN-6- Initial Antibiotic Select', -1),
(2, 1, 'CMS190', 'VTE-2 Intensive Care Unit (ICU)', -1),
(2, 1, 'CMS22', 'Preventive Care and Screening:', -1),
(2, 1, 'CMS26V1', 'Home Management Plan of Care (H', -1),
(2, 1, 'CMS2', 'Preventive Care and Screening: S', -1),
(2, 1, 'CMS30', 'AMI-10 Statin Prescribed at Disc', -1),
(2, 1, 'CMS31', 'EHDI-1a - Hearing screening prio', -1),
(2, 1, 'CMS32', 'ED-3-Median time from ED arrival', -1),
(2, 1, 'CMS50', 'Closing the referral loop: rece', -1),
(2, 1, 'CMS52', 'HIV/AIDS: Pneumocystis jiroveci', -1),
(2, 1, 'CMS53', 'AMI-8a- Primary PCI Received Wit', -1),
(2, 1, 'CMS55', 'Emergency Department (ED)-1 Emer', -1),
(2, 1, 'CMS56', 'Functional status assessment for', -1),
(2, 1, 'CMS60', 'AMI-7a- Fibrinolytic Therapy Rec', -1),
(2, 1, 'CMS61', 'Preventive Care and Screening:', -1),
(2, 1, 'CMS62', 'HIV/AIDS: Medical Visit', -1),
(2, 1, 'CMS64', 'Preventive Care and Screening:', -1),
(2, 1, 'CMS65', 'Hypertension: Improvement in bl', -1),
(2, 1, 'CMS66', 'Functional status assessment for', -1),
(2, 1, 'CMS68', 'Documentation of Current Medicat', -1),
(2, 1, 'CMS69', 'Preventive Care and Screening: B', -1),
(2, 1, 'CMS71', 'Stroke-3 Ischemic stroke - Antic', -1),
(2, 1, 'CMS72', 'Stroke-5 Ischemic stroke - Antit', -1),
(2, 1, 'CMS73', 'VTE-3 VTE Patients with Anticoag', -1),
(2, 1, 'CMS74', 'Primary Caries Prevention Interv', -1),
(2, 1, 'CMS75', 'Children who have dental decay o', -1),
(2, 1, 'CMS77', 'HIV/AIDS: RNA control for Patie', -1),
(2, 1, 'CMS82', 'Maternal depression screening', -1),
(2, 1, 'CMS90', 'Functional status assessment for', -1),
(2, 1, 'CMS91', 'Stroke-4 Ischemic stroke - Throm', -1),
(2, 1, 'CMS9V1', 'Exclusive Breast Milk Feeding', -1);

INSERT INTO openchpl.cqm_version (cqm_criterion_id, version, last_modified_user) VALUES
(60, 'v0', -1),(60, 'v1', -1),(60, 'v2', -1),(60, 'v3', -1),(60, 'v4', -1),
(61, 'v0', -1),(61, 'v1', -1),(61, 'v2', -1),(61, 'v3', -1),(61, 'v4', -1),
(62, 'v0', -1),(62, 'v1', -1),(62, 'v2', -1),(62, 'v3', -1),(62, 'v4', -1),
(63, 'v0', -1),(63, 'v1', -1),(63, 'v2', -1),(63, 'v3', -1),(63, 'v4', -1),
(64, 'v0', -1),(64, 'v1', -1),(64, 'v2', -1),(64, 'v3', -1),(64, 'v4', -1),
(65, 'v0', -1),(65, 'v1', -1),(65, 'v2', -1),(65, 'v3', -1),(65, 'v4', -1),
(66, 'v0', -1),(66, 'v1', -1),(66, 'v2', -1),(66, 'v3', -1),(66, 'v4', -1),
(67, 'v0', -1),(67, 'v1', -1),(67, 'v2', -1),(67, 'v3', -1),(67, 'v4', -1),
(68, 'v0', -1),(68, 'v1', -1),(68, 'v2', -1),(68, 'v3', -1),(68, 'v4', -1),
(69, 'v0', -1),(69, 'v1', -1),(69, 'v2', -1),(69, 'v3', -1),(69, 'v4', -1),
(70, 'v0', -1),(70, 'v1', -1),(70, 'v2', -1),(70, 'v3', -1),(70, 'v4', -1),
(71, 'v0', -1),(71, 'v1', -1),(71, 'v2', -1),(71, 'v3', -1),(71, 'v4', -1),
(72, 'v0', -1),(72, 'v1', -1),(72, 'v2', -1),(72, 'v3', -1),(72, 'v4', -1),
(73, 'v0', -1),(73, 'v1', -1),(73, 'v2', -1),(73, 'v3', -1),(73, 'v4', -1),
(74, 'v0', -1),(74, 'v1', -1),(74, 'v2', -1),(74, 'v3', -1),(74, 'v4', -1),
(75, 'v0', -1),(75, 'v1', -1),(75, 'v2', -1),(75, 'v3', -1),(75, 'v4', -1),
(76, 'v0', -1),(76, 'v1', -1),(76, 'v2', -1),(76, 'v3', -1),(76, 'v4', -1),
(77, 'v0', -1),(77, 'v1', -1),(77, 'v2', -1),(77, 'v3', -1),(77, 'v4', -1),
(78, 'v0', -1),(78, 'v1', -1),(78, 'v2', -1),(78, 'v3', -1),(78, 'v4', -1),
(79, 'v0', -1),(79, 'v1', -1),(79, 'v2', -1),(79, 'v3', -1),(79, 'v4', -1),
(80, 'v0', -1),(80, 'v1', -1),(80, 'v2', -1),(80, 'v3', -1),(80, 'v4', -1),
(81, 'v0', -1),(81, 'v1', -1),(81, 'v2', -1),(81, 'v3', -1),(81, 'v4', -1),
(82, 'v0', -1),(82, 'v1', -1),(82, 'v2', -1),(82, 'v3', -1),(82, 'v4', -1),
(83, 'v0', -1),(83, 'v1', -1),(83, 'v2', -1),(83, 'v3', -1),(83, 'v4', -1),
(84, 'v0', -1),(84, 'v1', -1),(84, 'v2', -1),(84, 'v3', -1),(84, 'v4', -1),
(85, 'v0', -1),(85, 'v1', -1),(85, 'v2', -1),(85, 'v3', -1),(85, 'v4', -1),
(86, 'v0', -1),(86, 'v1', -1),(86, 'v2', -1),(86, 'v3', -1),(86, 'v4', -1),
(87, 'v0', -1),(87, 'v1', -1),(87, 'v2', -1),(87, 'v3', -1),(87, 'v4', -1),
(88, 'v0', -1),(88, 'v1', -1),(88, 'v2', -1),(88, 'v3', -1),(88, 'v4', -1),
(89, 'v0', -1),(89, 'v1', -1),(89, 'v2', -1),(89, 'v3', -1),(89, 'v4', -1),
(90, 'v0', -1),(90, 'v1', -1),(90, 'v2', -1),(90, 'v3', -1),(90, 'v4', -1),
(91, 'v0', -1),(91, 'v1', -1),(91, 'v2', -1),(91, 'v3', -1),(91, 'v4', -1),
(92, 'v0', -1),(92, 'v1', -1),(92, 'v2', -1),(92, 'v3', -1),(92, 'v4', -1),
(93, 'v0', -1),(93, 'v1', -1),(93, 'v2', -1),(93, 'v3', -1),(93, 'v4', -1),
(94, 'v0', -1),(94, 'v1', -1),(94, 'v2', -1),(94, 'v3', -1),(94, 'v4', -1),
(95, 'v0', -1),(95, 'v1', -1),(95, 'v2', -1),(95, 'v3', -1),(95, 'v4', -1),
(96, 'v0', -1),(96, 'v1', -1),(96, 'v2', -1),(96, 'v3', -1),(96, 'v4', -1),
(97, 'v0', -1),(97, 'v1', -1),(97, 'v2', -1),(97, 'v3', -1),(97, 'v4', -1),
(98, 'v0', -1),(98, 'v1', -1),(98, 'v2', -1),(98, 'v3', -1),(98, 'v4', -1),
(99, 'v0', -1),(99, 'v1', -1),(99, 'v2', -1),(99, 'v3', -1),(99, 'v4', -1),
(100, 'v0', -1),(100, 'v1', -1),(100, 'v2', -1),(100, 'v3', -1),(100, 'v4', -1),
(101, 'v0', -1),(101, 'v1', -1),(101, 'v2', -1),(101, 'v3', -1),(101, 'v4', -1),
(102, 'v0', -1),(102, 'v1', -1),(102, 'v2', -1),(102, 'v3', -1),(102, 'v4', -1),
(103, 'v0', -1),(103, 'v1', -1),(103, 'v2', -1),(103, 'v3', -1),(103, 'v4', -1),
(104, 'v0', -1),(104, 'v1', -1),(104, 'v2', -1),(104, 'v3', -1),(104, 'v4', -1),
(105, 'v0', -1),(105, 'v1', -1),(105, 'v2', -1),(105, 'v3', -1),(105, 'v4', -1),
(106, 'v0', -1),(106, 'v1', -1),(106, 'v2', -1),(106, 'v3', -1),(106, 'v4', -1),
(107, 'v0', -1),(107, 'v1', -1),(107, 'v2', -1),(107, 'v3', -1),(107, 'v4', -1),
(108, 'v0', -1),(108, 'v1', -1),(108, 'v2', -1),(108, 'v3', -1),(108, 'v4', -1),
(109, 'v0', -1),(109, 'v1', -1),(109, 'v2', -1),(109, 'v3', -1),(109, 'v4', -1),
(110, 'v0', -1),(110, 'v1', -1),(110, 'v2', -1),(110, 'v3', -1),(110, 'v4', -1),
(111, 'v0', -1),(111, 'v1', -1),(111, 'v2', -1),(111, 'v3', -1),(111, 'v4', -1),
(112, 'v0', -1),(112, 'v1', -1),(112, 'v2', -1),(112, 'v3', -1),(112, 'v4', -1),
(113, 'v0', -1),(113, 'v1', -1),(113, 'v2', -1),(113, 'v3', -1),(113, 'v4', -1),
(114, 'v0', -1),(114, 'v1', -1),(114, 'v2', -1),(114, 'v3', -1),(114, 'v4', -1),
(115, 'v0', -1),(115, 'v1', -1),(115, 'v2', -1),(115, 'v3', -1),(115, 'v4', -1),
(116, 'v0', -1),(116, 'v1', -1),(116, 'v2', -1),(116, 'v3', -1),(116, 'v4', -1),
(117, 'v0', -1),(117, 'v1', -1),(117, 'v2', -1),(117, 'v3', -1),(117, 'v4', -1),
(118, 'v0', -1),(118, 'v1', -1),(118, 'v2', -1),(118, 'v3', -1),(118, 'v4', -1),
(119, 'v0', -1),(119, 'v1', -1),(119, 'v2', -1),(119, 'v3', -1),(119, 'v4', -1),
(120, 'v0', -1),(120, 'v1', -1),(120, 'v2', -1),(120, 'v3', -1),(120, 'v4', -1),
(121, 'v0', -1),(121, 'v1', -1),(121, 'v2', -1),(121, 'v3', -1),(121, 'v4', -1),
(122, 'v0', -1),(122, 'v1', -1),(122, 'v2', -1),(122, 'v3', -1),(122, 'v4', -1),
(123, 'v0', -1),(123, 'v1', -1),(123, 'v2', -1),(123, 'v3', -1),(123, 'v4', -1),
(124, 'v0', -1),(124, 'v1', -1),(124, 'v2', -1),(124, 'v3', -1),(124, 'v4', -1),
(125, 'v0', -1),(125, 'v1', -1),(125, 'v2', -1),(125, 'v3', -1),(125, 'v4', -1),
(126, 'v0', -1),(126, 'v1', -1),(126, 'v2', -1),(126, 'v3', -1),(126, 'v4', -1),
(127, 'v0', -1),(127, 'v1', -1),(127, 'v2', -1),(127, 'v3', -1),(127, 'v4', -1),
(128, 'v0', -1),(128, 'v1', -1),(128, 'v2', -1),(128, 'v3', -1),(128, 'v4', -1),
(129, 'v0', -1),(129, 'v1', -1),(129, 'v2', -1),(129, 'v3', -1),(129, 'v4', -1),
(130, 'v0', -1),(130, 'v1', -1),(130, 'v2', -1),(130, 'v3', -1),(130, 'v4', -1),
(131, 'v0', -1),(131, 'v1', -1),(131, 'v2', -1),(131, 'v3', -1),(131, 'v4', -1),
(132, 'v0', -1),(132, 'v1', -1),(132, 'v2', -1),(132, 'v3', -1),(132, 'v4', -1),
(133, 'v0', -1),(133, 'v1', -1),(133, 'v2', -1),(133, 'v3', -1),(133, 'v4', -1),
(134, 'v0', -1),(134, 'v1', -1),(134, 'v2', -1),(134, 'v3', -1),(134, 'v4', -1),
(135, 'v0', -1),(135, 'v1', -1),(135, 'v2', -1),(135, 'v3', -1),(135, 'v4', -1),
(136, 'v0', -1),(136, 'v1', -1),(136, 'v2', -1),(136, 'v3', -1),(136, 'v4', -1),
(137, 'v0', -1),(137, 'v1', -1),(137, 'v2', -1),(137, 'v3', -1),(137, 'v4', -1),
(138, 'v0', -1),(138, 'v1', -1),(138, 'v2', -1),(138, 'v3', -1),(138, 'v4', -1),
(139, 'v0', -1),(139, 'v1', -1),(139, 'v2', -1),(139, 'v3', -1),(139, 'v4', -1),
(140, 'v0', -1),(140, 'v1', -1),(140, 'v2', -1),(140, 'v3', -1),(140, 'v4', -1),
(141, 'v0', -1),(141, 'v1', -1),(141, 'v2', -1),(141, 'v3', -1),(141, 'v4', -1),
(142, 'v0', -1),(142, 'v1', -1),(142, 'v2', -1),(142, 'v3', -1),(142, 'v4', -1),
(143, 'v0', -1),(143, 'v1', -1),(143, 'v2', -1),(143, 'v3', -1),(143, 'v4', -1),
(144, 'v0', -1),(144, 'v1', -1),(144, 'v2', -1),(144, 'v3', -1),(144, 'v4', -1),
(145, 'v0', -1),(145, 'v1', -1),(145, 'v2', -1),(145, 'v3', -1),(145, 'v4', -1),
(146, 'v0', -1),(146, 'v1', -1),(146, 'v2', -1),(146, 'v3', -1),(146, 'v4', -1),
(147, 'v0', -1),(147, 'v1', -1),(147, 'v2', -1),(147, 'v3', -1),(147, 'v4', -1),
(148, 'v0', -1),(148, 'v1', -1),(148, 'v2', -1),(148, 'v3', -1),(148, 'v4', -1),
(149, 'v0', -1),(149, 'v1', -1),(149, 'v2', -1),(149, 'v3', -1),(149, 'v4', -1),
(150, 'v0', -1),(150, 'v1', -1),(150, 'v2', -1),(150, 'v3', -1),(150, 'v4', -1),
(151, 'v0', -1),(151, 'v1', -1),(151, 'v2', -1),(151, 'v3', -1),(151, 'v4', -1),
(152, 'v0', -1),(152, 'v1', -1),(152, 'v2', -1),(152, 'v3', -1),(152, 'v4', -1);
