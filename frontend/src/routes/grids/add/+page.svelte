<script lang="ts">
  import BackgroundLayout from "$lib/components/BackgroundLayout.svelte";
  import Navbar from "$lib/components/Navbar.svelte";
  import AddCard from "$lib/components/AddCard.svelte";
  import BackButton from "$lib/components/BackButton.svelte";
  import LeaderboardButton from "$lib/components/LeaderboardButton.svelte";
  import LeaveConfirmationDialog from "$lib/components/LeaveConfirmationDialog.svelte";
  import { goto } from "$app/navigation";
  import { onMount } from "svelte";
  import { gridService, type Category } from "$lib/services/api";
  import { authService } from "$lib/services/authService";
  import { API_BASE_URL } from "$lib/config";

  let isGridPanelOpen: boolean = true;
  let isLeaveDialogVisible: boolean = false;
  let navigateTo: string = '';
  let gridName: string = '';
  let errorMessage: string = '';
  let isLoading: boolean = false;
  let isAuthenticated: boolean = false;
  let accountId: number | null = null;
  let categories: Category[] = [];
  let selectedCategoryId: number | null = null;
  let newCategoryName: string = '';
  let isCategoryCreating: boolean = false;
  let showNewCategoryInput: boolean = false;
  
  let characters: Array<{name: string, imageUrl?: string, file?: File}> = Array(32).fill(null).map(() => ({ name: "" }));
  
  onMount(async () => {
    isAuthenticated = authService.isAuthenticated();
    
    const userInfo = authService.getUserInfo();
    console.log('Current user authentication status:', {
      isAuthenticated,
      userInfo
    });
    
    if (!isAuthenticated) {
      errorMessage = 'Vous devez être connecté pour créer une grille';
      return;
    }
    
    if (!userInfo.id) {
      console.error('No user ID found in localStorage');
      errorMessage = 'Erreur d\'authentification: ID utilisateur manquant';
      return;
    }
    
    try {
      console.log(`Getting account info for player ID ${userInfo.id}...`);
      
      const userCheckResponse = await fetch(`${API_BASE_URL}/accounts/player/${userInfo.id}`, {
        headers: authService.getAuthHeader()
      });
      
      if (!userCheckResponse.ok) {
        console.error(`No account found for player ID ${userInfo.id}`);
        errorMessage = 'Erreur d\'authentification: Compte utilisateur non trouvé';
        authService.logout();
        return;
      }
      
      const accountData = await userCheckResponse.json();
      console.log('Account data retrieved:', accountData);
      accountId = accountData.accountId;
      
      const response = await fetch(`${API_BASE_URL}/categories`, {
        headers: authService.getAuthHeader()
      });
      
      if (response.ok) {
        categories = await response.json();
        console.log('Loaded categories:', categories);
        
        if (categories.length > 0) {
          selectedCategoryId = categories[0].categoryId;
          console.log('Selected category ID:', selectedCategoryId);
        }
      } else {
        console.error('Failed to fetch categories');
      }
    } catch (error) {
      console.error('Error in grid creation setup:', error);
    } 
  });
  
  async function loadCategories() {
    try {
      const response = await fetch(`${API_BASE_URL}/categories`, {
        headers: authService.getAuthHeader()
      });
      
      if (!response.ok) {
        throw new Error('Failed to load categories');
      }
      
      categories = await response.json();
      
      if (categories.length > 0) {
        selectedCategoryId = categories[0].categoryId;
      }
    } catch (error) {
      console.error('Error loading categories:', error);
      categories = [];
    }
  }
  
  async function createCategory() {
    if (!newCategoryName.trim()) {
      errorMessage = 'Veuillez entrer un nom pour la catégorie';
      return;
    }
    
    try {
      isCategoryCreating = true;
      errorMessage = '';
      
      if (accountId === null) {
        console.error('Account ID not found');
        errorMessage = 'Erreur d\'authentification: Compte utilisateur non trouvé';
        return;
      }
      
      const createCategoryRequest = {
        name: newCategoryName,
        creatorId: accountId
      };
      
      console.log('Creating category with account ID:', accountId);
      
      console.log('Sending category creation request:', createCategoryRequest);
      
      const response = await fetch(`${API_BASE_URL}/categories`, {
        method: 'POST',
        headers: {
          ...authService.getAuthHeader(),
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(createCategoryRequest)
      });
      
      if (response.status === 409) {
        errorMessage = `La catégorie "${newCategoryName}" existe déjà`;
        const existingCategory = categories.find(cat => cat.name === newCategoryName);
        if (existingCategory) {
          selectedCategoryId = existingCategory.categoryId;
          showNewCategoryInput = false;
        }
        return;
      } else if (!response.ok) {
        const status = response.status;
        let message;
        try {
          const errorData = await response.json();
          message = errorData.message || `Erreur ${status}`;
        } catch (e) {
          message = `Erreur ${status}`;
        }
        throw new Error(`Échec de création de catégorie: ${message}`);
      }
      
      const newCategory = await response.json();
      console.log('Category created successfully:', newCategory);
      categories = [...categories, newCategory];
      selectedCategoryId = newCategory.categoryId;
      newCategoryName = '';
      showNewCategoryInput = false;
    } catch (error) {
      console.error('Error creating category:', error);
      errorMessage = error instanceof Error ? error.message : 'Une erreur est survenue lors de la création de la catégorie';
    } finally {
      isCategoryCreating = false;
    }
  }
  
  function toggleNewCategoryInput() {
    showNewCategoryInput = !showNewCategoryInput;
    if (!showNewCategoryInput) {
      newCategoryName = '';
    }
  }

  function handleLeaderboardClick() {
    console.log('Navigate to leaderboard');
  }

  function handleBackClick() {
    isLeaveDialogVisible = true;
    navigateTo = '/';
  }

  function confirmLeave() {
    isLeaveDialogVisible = false;
    goto(navigateTo);
  }

  function cancelLeave() {
    isLeaveDialogVisible = false;
    navigateTo = '';
  }

  function handleAvatarClick() {
    console.log('Open avatar/profile menu');
  }

  function handleAddCharacter(index: number, name: string, imageFile?: File) {
    console.log(`Adding/editing character at index ${index} with name: ${name}`);
    
    characters[index].name = name;
    
    if (imageFile) {
      characters[index].file = imageFile;
      characters[index].imageUrl = URL.createObjectURL(imageFile);
    }
    
    characters = [...characters];
  }
  
  function getFilledCharacterCount(): number {
    return characters.filter(char => char.name && char.imageUrl).length;
  }
  
  function canCreateGrid(): boolean {
    const filledCount = getFilledCharacterCount();
    return filledCount >= 6;
  }
  
  async function handleCreateGrid() {
    if (!isAuthenticated) {
      errorMessage = 'Vous devez être connecté pour créer une grille';
      return;
    }
    
    if (!canCreateGrid()) {
      errorMessage = 'Vous devez ajouter au moins 6 personnages pour créer votre grille.';
      return;
    }
    
    if (!gridName) {
      errorMessage = 'Veuillez donner un nom à votre grille.';
      return;
    }
    
    if (!selectedCategoryId && !newCategoryName) {
      let isAuthenticated = false;
      let errorMessage = '';
      let isLoading = false;
      let gridName = '';
      let accountId: number | null = null;
      let currentUserInfo = null;
      console.log('isAuthenticated:', isAuthenticated);
      console.log('errorMessage:', errorMessage);
      console.log('isLoading:', isLoading);
      console.log('gridName:', gridName);
      console.log('accountId:', accountId);
      console.log('currentUserInfo:', currentUserInfo);
      errorMessage = 'Veuillez sélectionner ou créer une catégorie.';
    }
    
    errorMessage = '';
    isLoading = true;
    
    const filledCharacters = characters.filter(char => 
      char.name && (char.imageUrl || char.file)
    ).map(char => ({
      name: char.name,
      imageUrl: char.imageUrl || '',
      file: char.file
    }));
    
    try {
      if (newCategoryName && !selectedCategoryId) {
        await createCategory();
      }
      
      if (selectedCategoryId) {
        if (accountId === null) {
          errorMessage = 'Erreur d\'authentification: Compte utilisateur non trouvé';
          return;
        }

        console.log('Creating grid with parameters:', {
          name: gridName,
          charactersCount: filledCharacters.length,
          categoryId: selectedCategoryId,
          accountId: accountId
        });
        
        try {
          await gridService.createGrid(gridName, filledCharacters, selectedCategoryId);
          
          console.log('Grid created, redirecting to grid list page');
          goto('/grids');
        } catch (gridError) {
          console.error('Grid creation error details:', gridError);
          throw gridError;
        }
      } else {
        throw new Error('No category selected');
      }
    } catch (error) {
      console.error('Error creating grid:', error);
      errorMessage = error instanceof Error ? error.message : 'Une erreur est survenue lors de la création de la grille';
    } finally {
      isLoading = false;
    }
  }

  function toggleGridPanel() {
    isLeaveDialogVisible = true;
    navigateTo = '/grids';
  }

  const customNavButtons = [
    {
      component: LeaderboardButton,
      props: { onClick: handleLeaderboardClick }
    },
    {
      component: BackButton,
      props: { onClick: handleBackClick }
    }
  ];
</script>

<BackgroundLayout>
  <div class="game-container">
    <div class="navbar-wrapper">
      <Navbar
        showLeaderboardButton={false}
        showGridButton={false}
        customButtons={customNavButtons}
        avatarUrl="https://placehold.co/88x88"
        avatarAlt="User avatar"
        onAvatarClick={handleAvatarClick}
      />
    </div>
    
    <div class="game-content">
      <div class="game-grid-container">
        <div class="game-grid">
          {#each characters as character, index}
            <div class="character-card">
              <AddCard 
                onClick={(name, imageFile) => handleAddCharacter(index, name, imageFile)}
                name={character.name}
                imageUrl={character.imageUrl || ''}
              />
            </div>
          {/each}
        </div>
      </div>
      
      <div class="grid-info-panel" class:open={isGridPanelOpen}>
        <div class="close-button" on:click={toggleGridPanel} on:keydown={(e) => e.key === 'Enter' && toggleGridPanel()} role="button" tabindex="0" aria-label="Toggle grid info panel">
          <svg width="35" height="35" viewBox="0 0 35 35" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect x="1.5" y="1.5" width="32" height="32" rx="3.5" fill="#DA6767" stroke="#A75A5A" stroke-width="3"/>
            <path d="M9.47523 9.47523C9.77962 9.17094 10.1924 9 10.6228 9C11.0532 9 11.466 9.17094 11.7704 9.47523L17.5098 15.2147L23.2492 9.47523C23.5554 9.17957 23.9654 9.01596 24.391 9.01966C24.8165 9.02336 25.2236 9.19406 25.5246 9.49501C25.8255 9.79595 25.9962 10.2031 25.9999 10.6286C26.0036 11.0542 25.84 11.4642 25.5444 11.7704L19.8049 17.5098L25.5444 23.2492C25.84 23.5554 26.0036 23.9654 25.9999 24.391C25.9962 24.8165 25.8255 25.2236 25.5246 25.5246C25.2236 25.8255 24.8165 25.9962 24.391 25.9999C23.9654 26.0036 23.5554 25.84 23.2492 25.5444L17.5098 19.8049L11.7704 25.5444C11.4642 25.84 11.0542 26.0036 10.6286 25.9999C10.2031 25.9962 9.79595 25.8255 9.49501 25.5246C9.19406 25.2236 9.02336 24.8165 9.01966 24.391C9.01596 23.9654 9.17957 23.5554 9.47523 23.2492L15.2147 17.5098L9.47523 11.7704C9.17094 11.466 9 11.0532 9 10.6228C9 10.1924 9.17094 9.77962 9.47523 9.47523Z" fill="white"/>
          </svg>
        </div>
        
        <div class="panel-content">
          <h2>Créer votre grille</h2>
          
          <div class="form-group">
            <label for="grid-name">Nom de la grille:</label>
            <input 
              type="text" 
              id="grid-name" 
              bind:value={gridName} 
              placeholder="Ma super grille" 
            />
          </div>
          
          <div class="form-group">
            <label for="category-select">Catégorie:</label>
            <div class="category-controls">
              <select 
                id="category-select" 
                bind:value={selectedCategoryId}
                disabled={showNewCategoryInput || categories.length === 0}
              >
                {#if categories.length === 0}
                  <option value={null}>Aucune catégorie disponible</option>
                {:else}
                  {#each categories as category}
                    <option value={category.categoryId}>{category.name}</option>
                  {/each}
                {/if}
              </select>
              
              <button 
                class="toggle-category-button" 
                on:click={toggleNewCategoryInput}
                type="button"
                data-icon={showNewCategoryInput ? '×' : '+'}
              >
                {showNewCategoryInput ? 'Annuler' : 'Nouvelle catégorie'}
              </button>
            </div>
          </div>
          
          {#if showNewCategoryInput}
            <div class="form-group new-category-group">
              <label for="new-category">Nouvelle catégorie:</label>
              <div class="new-category-controls">
                <input 
                  type="text" 
                  id="new-category" 
                  bind:value={newCategoryName} 
                  placeholder="Politiciens" 
                />
                <button 
                  class="create-category-button" 
                  on:click={createCategory}
                  disabled={isCategoryCreating || !newCategoryName.trim()}
                  type="button"
                >
                  {isCategoryCreating ? 'Création...' : 'Créer'}
                </button>
              </div>
            </div>
          {/if}
          
          <div class="grid-stats">
            <p>Personnages ajoutés: <span class="stat-value">{characters.filter(char => char.name && char.imageUrl).length}/32</span></p>
            <p class="min-required">Minimum requis: 6</p>
          </div>
          
          {#if errorMessage}
            <div class="error-message">{errorMessage}</div>
          {/if}
          
          <button 
            class="create-grid-button" 
            on:click={handleCreateGrid}
            disabled={!isAuthenticated || isLoading}
          >
            {#if isLoading}
              Création en cours...
            {:else}
              Créer
            {/if}
          </button>
          
          {#if !isAuthenticated}
            <div class="auth-warning">
              <p>Vous devez être <a href="/auth/login">connecté</a> pour créer une grille.</p>
              <p>Pas encore de compte ? <a href="/auth/register">Inscrivez-vous</a>.</p>
            </div>
          {/if}
        </div>
      </div>
    </div>
  </div>
  
  <LeaveConfirmationDialog 
    isVisible={isLeaveDialogVisible}
    onClose={cancelLeave}
    onLeave={confirmLeave}
  />
</BackgroundLayout>

<style>
  :global(body), :global(html) {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Comic Neue', sans-serif;
  }

  .game-container {
    width: 100%;
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
  }
  
  .navbar-wrapper {
    width: 100%;
    height: 85px;
    margin-bottom: 20px;
  }
  
  .game-content {
    display: flex;
    gap: 21px;
    justify-content: center;
    align-items: flex-start;
    width: 100%;
    max-width: 1620px;
    margin: 20px auto 0;
  }
  
  .game-grid-container {
    flex: 0 1 1073px;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .game-grid {
    display: grid;
    grid-template-columns: repeat(8, 1fr);
    grid-template-rows: repeat(4, auto);
    gap: 15px;
    justify-content: center;
    width: 100%;
    max-width: 1073px;
    height: 710px;
    padding: 4px;
  }
  
  .character-card {
    cursor: pointer;
  }

  .grid-info-panel {
    flex: 0 0 526px;
    width: 526px;
    height: 710px;
    background: rgba(255, 255, 255, 0.95);
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.2);
    border-radius: 10px;
    border: 4px solid rgba(92, 107, 192, 0.3);
    position: relative;
    padding: 30px;
    overflow: hidden;
    backdrop-filter: blur(10px);
    color: #333;
  }

  .close-button {
    position: absolute;
    top: 17px;
    right: 17px;
    cursor: pointer;
    z-index: 10;
  }
  
  .panel-content {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 15px;
  }
  
  .panel-content h2 {
    text-align: center;
    margin-bottom: 30px;
    color: #333;
    font-family: 'Comic Neue', sans-serif;
    font-size: 28px;
    text-shadow: 0px 1px 2px rgba(0, 0, 0, 0.1);
    position: relative;
  }
  
  .panel-content h2::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 3px;
    background: linear-gradient(90deg, rgba(92, 107, 192, 0.6), rgba(92, 107, 192, 0.9));
    border-radius: 3px;
  }
  
  .form-group {
    margin-bottom: 24px;
    position: relative;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 10px;
    font-weight: bold;
    color: #555;
    font-family: 'Comic Neue', sans-serif;
    font-size: 18px;
    letter-spacing: 0.5px;
  }
  
  .form-group input {
    width: 100%;
    padding: 12px 15px;
    background-color: rgba(250, 250, 250, 0.9);
    border: 2px solid rgba(92, 107, 192, 0.25);
    border-radius: 6px;
    font-size: 16px;
    color: #333;
    font-family: 'Roboto Mono', monospace;
  }
  
  .form-group input:focus {
    border-color: rgba(92, 107, 192, 0.6);
    outline: none;
  }
  
  .form-group input::placeholder {
    color: rgba(0, 0, 0, 0.3);
  }
  
  .grid-stats {
    margin: 24px 0;
    padding: 16px;
    background: rgba(92, 107, 192, 0.08);
    border-radius: 8px;
    border-left: 5px solid rgba(92, 107, 192, 0.5);
    border-top: 1px solid rgba(92, 107, 192, 0.2);
    border-right: 1px solid rgba(92, 107, 192, 0.2);
    border-bottom: 1px solid rgba(92, 107, 192, 0.2);
  }
  
  .grid-stats p {
    margin: 6px 0;
    font-family: 'Comic Neue', sans-serif;
    color: #555;
  }
  
  .stat-value {
    font-weight: bold;
    color: white;
    background: rgba(92, 107, 192, 0.6);
    padding: 2px 8px;
    border-radius: 12px;
    margin-left: 4px;
    font-family: 'Roboto Mono', monospace;
  }
  
  .min-required {
    font-size: 14px;
    color: #777;
    font-style: italic;
    display: flex;
    align-items: center;
  }
  
  .min-required::before {
    content: '⚠️';
    margin-right: 6px;
    font-size: 12px;
  }
  
  .error-message {
    padding: 12px 15px;
    background-color: rgba(255, 0, 0, 0.15);
    color: #ff6b6b;
    border-radius: 6px;
    margin-bottom: 20px;
    border-left: 3px solid #ff6b6b;
    font-family: 'Comic Neue', sans-serif;
    position: relative;
    padding-left: 35px;
  }
  
  .error-message::before {
    content: '⚠️';
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 14px;
  }
  
  .create-grid-button {
    margin-top: auto;
    padding: 14px 24px;
    background: linear-gradient(45deg, rgba(92, 107, 192, 0.8), rgba(63, 81, 181, 0.9));
    color: white;
    border: 3px solid rgba(63, 81, 181, 0.5);
    border-radius: 8px;
    font-size: 18px;
    font-family: 'Comic Neue', sans-serif;
    font-weight: bold;
    cursor: pointer;
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.2);
    letter-spacing: 0.5px;
    position: relative;
    overflow: hidden;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }
  
  .create-grid-button::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  }
  
  .create-grid-button:hover {
    background: linear-gradient(45deg, rgba(92, 107, 192, 0.9), rgba(63, 81, 181, 1));
    transform: translateY(-2px) scale(1.03);
    box-shadow: 0px 4px 0px rgba(0, 0, 0, 0.25);
  }
  
  .create-grid-button:hover::before {
    left: 100%;
  }
  
  .create-grid-button:disabled {
    background: #444;
    color: #888;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
    text-shadow: none;
  }
  
  .category-controls {
    display: flex;
    gap: 12px;
    align-items: center;
  }
  
  select {
    flex: 1;
    height: 45px;
    background-color: rgba(250, 250, 250, 0.9);
    color: #333;
    border: 2px solid rgba(92, 107, 192, 0.25);
    border-radius: 6px;
    padding: 0 15px;
    font-family: 'Roboto Mono', monospace;
    appearance: none;
    background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%235C6BC0' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolyline points='6 9 12 15 18 9'%3E%3C/polyline%3E%3C/svg%3E");
    background-repeat: no-repeat;
    background-position: right 10px center;
    background-size: 16px;
  }
  
  select:focus {
    border-color: rgba(92, 107, 192, 0.6);
    outline: none;
  }
  
  select:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background-color: rgba(200, 200, 200, 0.5);
  }
  
  .toggle-category-button {
    height: 45px;
    padding: 0 18px;
    background-color: rgba(92, 107, 192, 0.5);
    color: white;
    border: 2px solid rgba(92, 107, 192, 0.6);
    border-radius: 6px;
    cursor: pointer;
    font-family: 'Comic Neue', sans-serif;
    font-size: 14px;
    font-weight: bold;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0px 3px 0px rgba(0, 0, 0, 0.15);
    vertical-align: middle;
    line-height: 1;
  }
  
  .toggle-category-button::before {
    content: attr(data-icon);
    margin-right: 6px;
    font-size: 18px;
    font-weight: bold;
    display: inline-flex;
    align-items: center;
    line-height: 1;
    position: relative;
    top: -1px;
  }
  
  .toggle-category-button:hover {
    background-color: rgba(92, 107, 192, 0.7);
  }
  
  .toggle-category-button:active {
    background-color: rgba(92, 107, 192, 0.9);
  }
  
  .new-category-group {
    background-color: rgba(92, 107, 192, 0.08);
    padding: 20px;
    border-radius: 8px;
    margin-top: -5px;
    margin-bottom: 20px;
    border: 3px solid rgba(92, 107, 192, 0.3);
    box-shadow: inset 0 0 15px rgba(92, 107, 192, 0.05);
    animation: glow 2s infinite alternate;
  }
  
  @keyframes glow {
    from { box-shadow: inset 0 0 15px rgba(92, 107, 192, 0.05); }
    to { box-shadow: inset 0 0 20px rgba(92, 107, 192, 0.15); }
  }
  
  .new-category-controls {
    display: flex;
    gap: 12px;
  }
  
  .new-category-controls input {
    flex: 1;
  }
  
  .create-category-button {
    padding: 0 18px;
    background-color: rgba(92, 107, 192, 0.9);
    color: white;
    border: 3px solid rgba(92, 107, 192, 0.6);
    border-radius: 6px;
    cursor: pointer;
    font-family: 'Comic Neue', sans-serif;
    font-weight: bold;
    font-size: 14px;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 80px;
    position: relative;
    overflow: hidden;
  }
  
  .create-category-button::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  }
  
  .create-category-button:hover:not(:disabled) {
    background-color: #3F51B5;
    border-color: rgba(92, 107, 192, 0.8);
  }
  
  .create-category-button:hover:not(:disabled)::before {
    left: 100%;
  }
  
  .create-category-button:active:not(:disabled) {
    background-color: #283593;
  }
  
  .create-category-button:disabled {
    background-color: #444;
    color: #888;
    cursor: not-allowed;
    border-color: #555;
  }
  
  .auth-warning {
    margin-top: 24px;
    padding: 18px;
    border-radius: 10px;
    background-color: rgba(255, 107, 107, 0.2);
    color: white;
    text-align: center;
    border-left: 4px solid #ff6b6b;
    font-family: 'Comic Neue', sans-serif;
    position: relative;
    box-shadow: 0px 3px 0px rgba(0, 0, 0, 0.15);
  }
  
  .auth-warning::before {
    content: '🔒';
    position: absolute;
    top: 50%;
    left: 15px;
    transform: translateY(-50%);
    font-size: 20px;
  }
  
  .auth-warning p {
    margin: 6px 0;
    padding-left: 25px;
  }
  
  .auth-warning a {
    color: #ffcc00;
    text-decoration: none;
    font-weight: bold;
    padding: 2px 6px;
    border-radius: 4px;
    background-color: rgba(92, 107, 192, 0.1);
    display: inline-block;
  }
  
  .auth-warning a:hover {
    color: #ff9900;
    background-color: rgba(92, 107, 192, 0.2);
    transform: translateY(-1px);
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  }
  
  @media (max-width: 1600px) {
    .game-content {
      flex-direction: column;
      align-items: center;
    }
    
    .game-grid-container {
      flex: 0 1 auto;
      width: 100%;
      max-width: 550px;
    }
    
    .game-grid {
      grid-template-columns: repeat(4, 1fr);
      grid-template-rows: repeat(8, auto);
      max-width: 550px;
    }
    
    .grid-info-panel {
      margin-top: 20px;
      flex: 0 0 600px;
      width: 600px;
    }
  }
</style>