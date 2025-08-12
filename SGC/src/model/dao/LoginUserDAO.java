package model.dao;

import model.domain.Role;

public interface LoginUserDAO {
    public Role verificaCredenziali(String username, String password, int ID);
}
