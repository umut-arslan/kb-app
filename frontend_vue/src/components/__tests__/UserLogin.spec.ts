import UserLogin from '../UserLogin.vue'
import {mount} from "@vue/test-utils";
import { describe, it, expect, vi, afterEach } from 'vitest'
import type {User} from "../../types/User";
import UserService from '../../service/UserService'

const testUser: User = {
  name: 'testUserName',
  email: 'testMail@testD.com',
  avatarUrl: 'testUrl'
}

vi.mock('UserService')

afterEach(() => {
  vi.restoreAllMocks()
})

describe('User Info Tests', () => {

  describe('UserLogin test when logged in', () => {

      UserService.getUserDetails = vi.fn().mockResolvedValue(testUser);
      const wrapper = mount(UserLogin);

    it('UserLogin mounts properly', async () => {
      expect(wrapper).toBeTruthy();
      expect(await UserService.getUserDetails).toHaveBeenCalledOnce();
    });

    it('renders Users Info and logout button when logged in', () => {
      expect(wrapper.text()).toContain(testUser.name);
      expect(wrapper.text()).toContain('Logout');
      expect(wrapper.find('img').attributes('src')).toBe(testUser.avatarUrl);
    });
  })

  describe('UserLogin test when not logged in', () => {
    UserService.getUserDetails = vi.fn().mockResolvedValue(undefined);
    const wrapper = mount(UserLogin);

    it('renders no User Info but login button when not logged in', () => {
      expect(wrapper.text()).not.toContain(testUser.name);
      expect(wrapper.text()).toContain('Login');
    })
  })
})
