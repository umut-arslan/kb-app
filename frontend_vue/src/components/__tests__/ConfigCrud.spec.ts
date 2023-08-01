import ConfigCrud from '../ConfigCrud.vue'
import {mount} from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from 'vitest'
import type {Config} from "../../types/Config";
import ConfigService from '../../service/ConfigService'

const testConfigs: Config[] = [
    {
        id: 0,
        key: 'key1',
        value: 'val1'
    },
    {
        id: 1,
        key: 'key2',
        value: 'val2'
    },
    {
        id: 2,
        key: 'key3',
        value: 'val3'
    },
    {
        id: 3,
        key: 'key4',
        value: 'val4'
    },
]

vi.mock('ConfigService')

describe('Config crud test', () => {

    afterEach(() => {
      vi.restoreAllMocks()
    })

    let wrapper = mount(ConfigCrud);

    it('ConfigCrud mounts properly', async () => {
        ConfigService.getConfigsByUser = vi.fn().mockResolvedValue({data: testConfigs});

        wrapper = mount(ConfigCrud);

        expect(wrapper).toBeTruthy();
        expect(await ConfigService.getConfigsByUser).toHaveBeenCalledOnce();
    });

    it('renders ConfigCrud titles', () => {
        expect(wrapper.text()).toContain('Entwicklungstemplate');
        expect(wrapper.text()).toContain('Search Config by Key');
    });

    it('creates new Config on Button click', async () => {
        const spy = vi.spyOn(ConfigService, 'createConfig');

        const firstInput = wrapper.find('input[id="keyInputCreate"]');
        await firstInput.setValue('TestKey');
        const secondInput = wrapper.find('input[id="valInputCreate"]');
        await secondInput.setValue('TestVal');
        expect((firstInput.element as HTMLInputElement).value).toBe('TestKey');
        expect((secondInput.element as HTMLInputElement).value).toBe('TestVal');
        await wrapper.find('form[id="configCreateForm"]').trigger('submit.prevent')
        expect(await spy).toHaveBeenCalledOnce();
    });

    it('searchConfigByKey event called on submit', async () => {
        const spy = vi.spyOn(ConfigService, 'getConfigByKey');

        const firstSearch = wrapper.find('input[id="keyInputSearch"]');
        await firstSearch.setValue('testest');
        await wrapper.find('form[id="getConfigByKeyForm"]').trigger('submit.prevent')
        expect(await spy).toHaveBeenCalled();
    });

    it('finds existing config', async () => {
        const config = testConfigs[0];

        ConfigService.getConfigByKey = vi.fn().mockResolvedValue(config.value);

        const firstSearch = wrapper.find('input[id="keyInputSearch"]');
        await firstSearch.setValue(config.key);
        await wrapper.find('form[id="getConfigByKeyForm"]').trigger('submit.prevent')
        expect(wrapper.vm.searchVal).toBe(config.value);
    });

    it('does not find non-existing config', async () => {
        const key: string = 'NonExistingKey';
        const errorVal: string = 'Not Found!';

        ConfigService.getConfigByKey = vi.fn().mockResolvedValue('Not Found!');

        const firstSearch = wrapper.find('input[id="keyInputSearch"]');
        await firstSearch.setValue(key);
        await wrapper.find('form[id="getConfigByKeyForm"]').trigger('submit.prevent')
        expect(wrapper.vm.searchVal).toBe(errorVal);
    });

    it('deletes config', async () => {
        const deleteBtn = wrapper.findAll('button[id=deleteConfigBtn]').at(0);
        vi.spyOn(ConfigService, 'deleteConfig')
        await deleteBtn?.trigger('click');
        expect(deleteBtn).toBeTruthy();
        expect(await ConfigService.deleteConfig).toHaveBeenCalledOnce();
    });

    it('updates first existing config', async () => {
        const key = wrapper.findAll('input[id=keyInputUpdate]').at(0);
        const val = wrapper.findAll('input[id=valInputUpdate]').at(0);
        const form = wrapper.findAll('form[id=configElementForm]').at(0);
        await key?.setValue("changedKey");
        await val?.setValue("changedVal");
        expect(key).toBeTruthy();
        expect(val).toBeTruthy();
        expect(form).toBeTruthy();
        vi.spyOn(ConfigService, 'updateConfig');
        await form?.trigger('keydown.enter');
        expect(await ConfigService.updateConfig).toHaveBeenCalledOnce();
    });
})
