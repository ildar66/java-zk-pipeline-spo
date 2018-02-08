delete from ATTRIBUTES_REQUIRED where id_attributes_required in (620, 621, 710, 711);
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (620, '������������� ������� ������������� �������', '������������� ������� ������������� ��������� ������� (� �������)', '�������. ������ �������', 0, 'othCondType2_body', '���_�������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (621, null, '������������� ������� ������������� ��������� ������� (�������)', '�������. ������ �������', 0, 'otherConditionType2', '�������������_�������_������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (710, '������������� ������� ���������� ������', '������������� ������� ���������� ������ (� �������)', '�������. ������ �������', 0, 'othCondType1_body', '���_�������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (711, null, '������������� ������� ���������� ������ (�������)', '�������. ������ �������', 0, 'otherConditionType1', '�������������_�������_������');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required >= 1718 and id_attributes_required <=1722;
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1718, null, '��� ������ ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'fact_premiumForPrint', '��_��������������_���������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1719, null, '������������ ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'fact_premiumType', '��_��������������_���');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1720, null, '����� ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'fact_premiumTypeValue', '��_��������������_�����');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1721, null, '������ ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'fact_premiumcurr', '��_��������������_������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1722, null, '������� ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'fact_premiumtext', '��_��������������_�������');
commit;
delete from ATTRIBUTES_REQUIRED where id_attributes_required >= 1768 and id_attributes_required <=1772;
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1768, null, '����� � ������ ��� ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'factShort_premiumForPrint', '��2_��������������_���������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1769, null, '������������ ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'factShort_premiumType', '��2_��������������_���');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1770, null, '����� ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'factShort_premiumTypeValue', '��2_��������������_�����');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1771, null, '������ ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'factShort_premiumcurr', '��2_��������������_������');
insert into ATTRIBUTES_REQUIRED (id_attributes_required, name, description, group_name, is_required, template_code, template_code_rus)
values (1772, null, '������� ��������������(������� ����.�������� ���������� ������)', '����������� ������� ������. ���������� ������', 0, 'factShort_premiumtext', '��2_��������������_�������');
commit;