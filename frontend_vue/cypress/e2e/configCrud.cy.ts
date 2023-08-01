describe('test app initialization', () => {
  it('visits the app root url', () => {
    cy.visit('/')
    cy.contains('h1', 'kb-app')
  })
  it('get request on mount', () => {
    cy.intercept('GET', '/api/configs/user', []).as('getConfigs')
    cy.visit('/')
    cy.wait('@getConfigs').then((interception) =>{
      expect(interception.response?.body).to.deep.eq([]);
        }
    )
  });
})

const testKey : String = 'testKey'
const testVal : String = 'testVal'


describe('component testing', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/configs/user', [{
      idConfig: 1,
      key: testKey,
      value: testVal
    }]).as('getConfigs')

    cy.visit('/').wait('@getConfigs')
  });
  it('should create new config and show up on the config list', () => {
    const newKey = 'newKey'
    const newVal = 'newVal'

    cy.intercept('POST', '/api/configs', [{
      idConfig: 1,
      key: newKey,
      value: newVal
    }]).as('createConfig');

    cy.intercept('GET', '/api/configs/user', [{
      idConfig: 1,
      key: testKey,
      value: testVal
    },
      {
        idConfig: 2,
        key: newKey,
        value: newVal
      }]).as('getConfigs')

    cy.get('#keyInputCreate').type(newKey);
    cy.get('#valInputCreate').type(newVal);
    cy.get('#submitInputCreate').click().wait('@getConfigs');

    cy.get('ul>li').eq(1)
        .find('form')
        .find('#keyInputUpdate')
        .invoke('val')
        .should("contain", newKey);
    cy.get('ul>li').eq(1)
        .find('form')
        .find('#valInputUpdate')
        .invoke('val')
        .should("contain", newVal);
  });

  it('should update existing config and show updated config in config list', () => {
    const updatedKey = 'UpdatedKey'
    const updatedVal = 'UpdatedVal'

    cy.intercept('PUT', '/api/configs', []).as('updateConfig')
    cy.intercept('GET', '/api/configs/user', [{
      idConfig: 1,
      key: updatedKey,
      value: updatedVal
    }]).as('getConfigs')

    cy.get('ul>li').eq(0)
        .find('form')
        .find('#keyInputUpdate')
        .clear()
        .type(updatedKey)

    cy.get('ul>li').eq(0)
        .find('form')
        .find('#valInputUpdate')
        .clear()
        .type(updatedVal)
        .type('{enter}')
        .wait('@updateConfig')
        .wait('@getConfigs')


    cy.get('ul>li').eq(0)
      .find('form')
      .find('#keyInputUpdate')
      .invoke('val')
      .should("contain", updatedKey);

    cy.get('ul>li').eq(0)
      .find('form')
      .find('#valInputUpdate')
      .invoke('val')
      .should("contain", updatedVal);
  });

  it('should find existing config', () => {
    cy.intercept('GET', '/api/configs/' + testKey,{
      idConfig: 1,
      key: testKey,
      value: testVal
    }).as('findConfig')
    cy.get('#keyInputSearch').type(testKey.toString()).type('{enter}').wait('@findConfig')
    cy.get('#valSearchRespond').should('include.text', testVal)
  });

  it('should not find non-existing config', () => {
    const unknownKey : string = 'unknownKey'
    cy.intercept('GET', '/api/configs/' + unknownKey,{
      statusCode: 500
    }).as('findConfig')
    cy.get('#keyInputSearch').type(unknownKey).type('{enter}').wait('@findConfig')
    cy.get('#valSearchRespond').contains( "Not Found!")
  });

  it.skip('should delete created config', () => {
    cy.intercept('DELETE', '/api/configs/' + 1).as('deleteConfig')

    cy.get('ul>li').eq(0)
      .find('div')
      .find('#deleteConfigBtn')
      .click().wait('@deleteConfig');

    cy.get('ul>li')
      .should('have.length', 0);
  });

});
